package com.ztech.store.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.ztech.store.IntegrationTest;
import com.ztech.store.domain.CartItem;
import com.ztech.store.domain.Product;
import com.ztech.store.repository.CartItemRepository;
import com.ztech.store.service.EntityManager;
import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link CartItemResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class CartItemResourceIT {

    private static final Integer DEFAULT_QUANTITY = 1;
    private static final Integer UPDATED_QUANTITY = 2;

    private static final String ENTITY_API_URL = "/api/cart-items";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private CartItem cartItem;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CartItem createEntity(EntityManager em) {
        CartItem cartItem = new CartItem().quantity(DEFAULT_QUANTITY);
        // Add required entity
        Product product;
        product = em.insert(ProductResourceIT.createEntity(em)).block();
        cartItem.setProduct(product);
        return cartItem;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CartItem createUpdatedEntity(EntityManager em) {
        CartItem cartItem = new CartItem().quantity(UPDATED_QUANTITY);
        // Add required entity
        Product product;
        product = em.insert(ProductResourceIT.createUpdatedEntity(em)).block();
        cartItem.setProduct(product);
        return cartItem;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(CartItem.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        ProductResourceIT.deleteEntities(em);
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        cartItem = createEntity(em);
    }

    @Test
    void createCartItem() throws Exception {
        int databaseSizeBeforeCreate = cartItemRepository.findAll().collectList().block().size();
        // Create the CartItem
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(cartItem))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the CartItem in the database
        List<CartItem> cartItemList = cartItemRepository.findAll().collectList().block();
        assertThat(cartItemList).hasSize(databaseSizeBeforeCreate + 1);
        CartItem testCartItem = cartItemList.get(cartItemList.size() - 1);
        assertThat(testCartItem.getQuantity()).isEqualTo(DEFAULT_QUANTITY);
    }

    @Test
    void createCartItemWithExistingId() throws Exception {
        // Create the CartItem with an existing ID
        cartItem.setId(1L);

        int databaseSizeBeforeCreate = cartItemRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(cartItem))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CartItem in the database
        List<CartItem> cartItemList = cartItemRepository.findAll().collectList().block();
        assertThat(cartItemList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkQuantityIsRequired() throws Exception {
        int databaseSizeBeforeTest = cartItemRepository.findAll().collectList().block().size();
        // set the field null
        cartItem.setQuantity(null);

        // Create the CartItem, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(cartItem))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<CartItem> cartItemList = cartItemRepository.findAll().collectList().block();
        assertThat(cartItemList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllCartItemsAsStream() {
        // Initialize the database
        cartItemRepository.save(cartItem).block();

        List<CartItem> cartItemList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(CartItem.class)
            .getResponseBody()
            .filter(cartItem::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(cartItemList).isNotNull();
        assertThat(cartItemList).hasSize(1);
        CartItem testCartItem = cartItemList.get(0);
        assertThat(testCartItem.getQuantity()).isEqualTo(DEFAULT_QUANTITY);
    }

    @Test
    void getAllCartItems() {
        // Initialize the database
        cartItemRepository.save(cartItem).block();

        // Get all the cartItemList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(cartItem.getId().intValue()))
            .jsonPath("$.[*].quantity")
            .value(hasItem(DEFAULT_QUANTITY));
    }

    @Test
    void getCartItem() {
        // Initialize the database
        cartItemRepository.save(cartItem).block();

        // Get the cartItem
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, cartItem.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(cartItem.getId().intValue()))
            .jsonPath("$.quantity")
            .value(is(DEFAULT_QUANTITY));
    }

    @Test
    void getNonExistingCartItem() {
        // Get the cartItem
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewCartItem() throws Exception {
        // Initialize the database
        cartItemRepository.save(cartItem).block();

        int databaseSizeBeforeUpdate = cartItemRepository.findAll().collectList().block().size();

        // Update the cartItem
        CartItem updatedCartItem = cartItemRepository.findById(cartItem.getId()).block();
        updatedCartItem.quantity(UPDATED_QUANTITY);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedCartItem.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedCartItem))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the CartItem in the database
        List<CartItem> cartItemList = cartItemRepository.findAll().collectList().block();
        assertThat(cartItemList).hasSize(databaseSizeBeforeUpdate);
        CartItem testCartItem = cartItemList.get(cartItemList.size() - 1);
        assertThat(testCartItem.getQuantity()).isEqualTo(UPDATED_QUANTITY);
    }

    @Test
    void putNonExistingCartItem() throws Exception {
        int databaseSizeBeforeUpdate = cartItemRepository.findAll().collectList().block().size();
        cartItem.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, cartItem.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(cartItem))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CartItem in the database
        List<CartItem> cartItemList = cartItemRepository.findAll().collectList().block();
        assertThat(cartItemList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchCartItem() throws Exception {
        int databaseSizeBeforeUpdate = cartItemRepository.findAll().collectList().block().size();
        cartItem.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(cartItem))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CartItem in the database
        List<CartItem> cartItemList = cartItemRepository.findAll().collectList().block();
        assertThat(cartItemList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamCartItem() throws Exception {
        int databaseSizeBeforeUpdate = cartItemRepository.findAll().collectList().block().size();
        cartItem.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(cartItem))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the CartItem in the database
        List<CartItem> cartItemList = cartItemRepository.findAll().collectList().block();
        assertThat(cartItemList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateCartItemWithPatch() throws Exception {
        // Initialize the database
        cartItemRepository.save(cartItem).block();

        int databaseSizeBeforeUpdate = cartItemRepository.findAll().collectList().block().size();

        // Update the cartItem using partial update
        CartItem partialUpdatedCartItem = new CartItem();
        partialUpdatedCartItem.setId(cartItem.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCartItem.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCartItem))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the CartItem in the database
        List<CartItem> cartItemList = cartItemRepository.findAll().collectList().block();
        assertThat(cartItemList).hasSize(databaseSizeBeforeUpdate);
        CartItem testCartItem = cartItemList.get(cartItemList.size() - 1);
        assertThat(testCartItem.getQuantity()).isEqualTo(DEFAULT_QUANTITY);
    }

    @Test
    void fullUpdateCartItemWithPatch() throws Exception {
        // Initialize the database
        cartItemRepository.save(cartItem).block();

        int databaseSizeBeforeUpdate = cartItemRepository.findAll().collectList().block().size();

        // Update the cartItem using partial update
        CartItem partialUpdatedCartItem = new CartItem();
        partialUpdatedCartItem.setId(cartItem.getId());

        partialUpdatedCartItem.quantity(UPDATED_QUANTITY);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCartItem.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCartItem))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the CartItem in the database
        List<CartItem> cartItemList = cartItemRepository.findAll().collectList().block();
        assertThat(cartItemList).hasSize(databaseSizeBeforeUpdate);
        CartItem testCartItem = cartItemList.get(cartItemList.size() - 1);
        assertThat(testCartItem.getQuantity()).isEqualTo(UPDATED_QUANTITY);
    }

    @Test
    void patchNonExistingCartItem() throws Exception {
        int databaseSizeBeforeUpdate = cartItemRepository.findAll().collectList().block().size();
        cartItem.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, cartItem.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(cartItem))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CartItem in the database
        List<CartItem> cartItemList = cartItemRepository.findAll().collectList().block();
        assertThat(cartItemList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchCartItem() throws Exception {
        int databaseSizeBeforeUpdate = cartItemRepository.findAll().collectList().block().size();
        cartItem.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(cartItem))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the CartItem in the database
        List<CartItem> cartItemList = cartItemRepository.findAll().collectList().block();
        assertThat(cartItemList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamCartItem() throws Exception {
        int databaseSizeBeforeUpdate = cartItemRepository.findAll().collectList().block().size();
        cartItem.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(cartItem))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the CartItem in the database
        List<CartItem> cartItemList = cartItemRepository.findAll().collectList().block();
        assertThat(cartItemList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteCartItem() {
        // Initialize the database
        cartItemRepository.save(cartItem).block();

        int databaseSizeBeforeDelete = cartItemRepository.findAll().collectList().block().size();

        // Delete the cartItem
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, cartItem.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<CartItem> cartItemList = cartItemRepository.findAll().collectList().block();
        assertThat(cartItemList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
