package com.ztech.store.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.ztech.store.IntegrationTest;
import com.ztech.store.domain.Cart;
import com.ztech.store.domain.User;
import com.ztech.store.repository.CartRepository;
import com.ztech.store.service.EntityManager;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
 * Integration tests for the {@link CartResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class CartResourceIT {

    private static final Instant DEFAULT_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/carts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Cart cart;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Cart createEntity(EntityManager em) {
        Cart cart = new Cart().date(DEFAULT_DATE);
        // Add required entity
        User user = em.insert(UserResourceIT.createEntity(em)).block();
        cart.setUser(user);
        return cart;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Cart createUpdatedEntity(EntityManager em) {
        Cart cart = new Cart().date(UPDATED_DATE);
        // Add required entity
        User user = em.insert(UserResourceIT.createEntity(em)).block();
        cart.setUser(user);
        return cart;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Cart.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        UserResourceIT.deleteEntities(em);
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        cart = createEntity(em);
    }

    @Test
    void createCart() throws Exception {
        int databaseSizeBeforeCreate = cartRepository.findAll().collectList().block().size();
        // Create the Cart
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(cart))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Cart in the database
        List<Cart> cartList = cartRepository.findAll().collectList().block();
        assertThat(cartList).hasSize(databaseSizeBeforeCreate + 1);
        Cart testCart = cartList.get(cartList.size() - 1);
        assertThat(testCart.getDate()).isEqualTo(DEFAULT_DATE);
    }

    @Test
    void createCartWithExistingId() throws Exception {
        // Create the Cart with an existing ID
        cart.setId(1L);

        int databaseSizeBeforeCreate = cartRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(cart))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Cart in the database
        List<Cart> cartList = cartRepository.findAll().collectList().block();
        assertThat(cartList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = cartRepository.findAll().collectList().block().size();
        // set the field null
        cart.setDate(null);

        // Create the Cart, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(cart))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Cart> cartList = cartRepository.findAll().collectList().block();
        assertThat(cartList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllCartsAsStream() {
        // Initialize the database
        cartRepository.save(cart).block();

        List<Cart> cartList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Cart.class)
            .getResponseBody()
            .filter(cart::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(cartList).isNotNull();
        assertThat(cartList).hasSize(1);
        Cart testCart = cartList.get(0);
        assertThat(testCart.getDate()).isEqualTo(DEFAULT_DATE);
    }

    @Test
    void getAllCarts() {
        // Initialize the database
        cartRepository.save(cart).block();

        // Get all the cartList
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
            .value(hasItem(cart.getId().intValue()))
            .jsonPath("$.[*].date")
            .value(hasItem(DEFAULT_DATE.toString()));
    }

    @Test
    void getCart() {
        // Initialize the database
        cartRepository.save(cart).block();

        // Get the cart
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, cart.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(cart.getId().intValue()))
            .jsonPath("$.date")
            .value(is(DEFAULT_DATE.toString()));
    }

    @Test
    void getNonExistingCart() {
        // Get the cart
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewCart() throws Exception {
        // Initialize the database
        cartRepository.save(cart).block();

        int databaseSizeBeforeUpdate = cartRepository.findAll().collectList().block().size();

        // Update the cart
        Cart updatedCart = cartRepository.findById(cart.getId()).block();
        updatedCart.date(UPDATED_DATE);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedCart.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedCart))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Cart in the database
        List<Cart> cartList = cartRepository.findAll().collectList().block();
        assertThat(cartList).hasSize(databaseSizeBeforeUpdate);
        Cart testCart = cartList.get(cartList.size() - 1);
        assertThat(testCart.getDate()).isEqualTo(UPDATED_DATE);
    }

    @Test
    void putNonExistingCart() throws Exception {
        int databaseSizeBeforeUpdate = cartRepository.findAll().collectList().block().size();
        cart.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, cart.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(cart))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Cart in the database
        List<Cart> cartList = cartRepository.findAll().collectList().block();
        assertThat(cartList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchCart() throws Exception {
        int databaseSizeBeforeUpdate = cartRepository.findAll().collectList().block().size();
        cart.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(cart))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Cart in the database
        List<Cart> cartList = cartRepository.findAll().collectList().block();
        assertThat(cartList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamCart() throws Exception {
        int databaseSizeBeforeUpdate = cartRepository.findAll().collectList().block().size();
        cart.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(cart))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Cart in the database
        List<Cart> cartList = cartRepository.findAll().collectList().block();
        assertThat(cartList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateCartWithPatch() throws Exception {
        // Initialize the database
        cartRepository.save(cart).block();

        int databaseSizeBeforeUpdate = cartRepository.findAll().collectList().block().size();

        // Update the cart using partial update
        Cart partialUpdatedCart = new Cart();
        partialUpdatedCart.setId(cart.getId());

        partialUpdatedCart.date(UPDATED_DATE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCart.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCart))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Cart in the database
        List<Cart> cartList = cartRepository.findAll().collectList().block();
        assertThat(cartList).hasSize(databaseSizeBeforeUpdate);
        Cart testCart = cartList.get(cartList.size() - 1);
        assertThat(testCart.getDate()).isEqualTo(UPDATED_DATE);
    }

    @Test
    void fullUpdateCartWithPatch() throws Exception {
        // Initialize the database
        cartRepository.save(cart).block();

        int databaseSizeBeforeUpdate = cartRepository.findAll().collectList().block().size();

        // Update the cart using partial update
        Cart partialUpdatedCart = new Cart();
        partialUpdatedCart.setId(cart.getId());

        partialUpdatedCart.date(UPDATED_DATE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCart.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCart))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Cart in the database
        List<Cart> cartList = cartRepository.findAll().collectList().block();
        assertThat(cartList).hasSize(databaseSizeBeforeUpdate);
        Cart testCart = cartList.get(cartList.size() - 1);
        assertThat(testCart.getDate()).isEqualTo(UPDATED_DATE);
    }

    @Test
    void patchNonExistingCart() throws Exception {
        int databaseSizeBeforeUpdate = cartRepository.findAll().collectList().block().size();
        cart.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, cart.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(cart))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Cart in the database
        List<Cart> cartList = cartRepository.findAll().collectList().block();
        assertThat(cartList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchCart() throws Exception {
        int databaseSizeBeforeUpdate = cartRepository.findAll().collectList().block().size();
        cart.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(cart))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Cart in the database
        List<Cart> cartList = cartRepository.findAll().collectList().block();
        assertThat(cartList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamCart() throws Exception {
        int databaseSizeBeforeUpdate = cartRepository.findAll().collectList().block().size();
        cart.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(cart))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Cart in the database
        List<Cart> cartList = cartRepository.findAll().collectList().block();
        assertThat(cartList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteCart() {
        // Initialize the database
        cartRepository.save(cart).block();

        int databaseSizeBeforeDelete = cartRepository.findAll().collectList().block().size();

        // Delete the cart
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, cart.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Cart> cartList = cartRepository.findAll().collectList().block();
        assertThat(cartList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
