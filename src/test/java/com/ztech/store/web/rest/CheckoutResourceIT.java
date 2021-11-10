package com.ztech.store.web.rest;

import static com.ztech.store.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.ztech.store.IntegrationTest;
import com.ztech.store.domain.Cart;
import com.ztech.store.domain.Checkout;
import com.ztech.store.repository.CheckoutRepository;
import com.ztech.store.service.EntityManager;
import java.math.BigDecimal;
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
 * Integration tests for the {@link CheckoutResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class CheckoutResourceIT {

    private static final Integer DEFAULT_QUANTITY = 0;
    private static final Integer UPDATED_QUANTITY = 1;

    private static final BigDecimal DEFAULT_TOTAL_PRICE = new BigDecimal(0);
    private static final BigDecimal UPDATED_TOTAL_PRICE = new BigDecimal(1);

    private static final String ENTITY_API_URL = "/api/checkouts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CheckoutRepository checkoutRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Checkout checkout;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Checkout createEntity(EntityManager em) {
        Checkout checkout = new Checkout().quantity(DEFAULT_QUANTITY).totalPrice(DEFAULT_TOTAL_PRICE);
        // Add required entity
        Cart cart;
        cart = em.insert(CartResourceIT.createEntity(em)).block();
        checkout.setCart(cart);
        return checkout;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Checkout createUpdatedEntity(EntityManager em) {
        Checkout checkout = new Checkout().quantity(UPDATED_QUANTITY).totalPrice(UPDATED_TOTAL_PRICE);
        // Add required entity
        Cart cart;
        cart = em.insert(CartResourceIT.createUpdatedEntity(em)).block();
        checkout.setCart(cart);
        return checkout;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Checkout.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        CartResourceIT.deleteEntities(em);
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        checkout = createEntity(em);
    }

    @Test
    void createCheckout() throws Exception {
        int databaseSizeBeforeCreate = checkoutRepository.findAll().collectList().block().size();
        // Create the Checkout
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(checkout))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Checkout in the database
        List<Checkout> checkoutList = checkoutRepository.findAll().collectList().block();
        assertThat(checkoutList).hasSize(databaseSizeBeforeCreate + 1);
        Checkout testCheckout = checkoutList.get(checkoutList.size() - 1);
        assertThat(testCheckout.getQuantity()).isEqualTo(DEFAULT_QUANTITY);
        assertThat(testCheckout.getTotalPrice()).isEqualByComparingTo(DEFAULT_TOTAL_PRICE);
    }

    @Test
    void createCheckoutWithExistingId() throws Exception {
        // Create the Checkout with an existing ID
        checkout.setId(1L);

        int databaseSizeBeforeCreate = checkoutRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(checkout))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Checkout in the database
        List<Checkout> checkoutList = checkoutRepository.findAll().collectList().block();
        assertThat(checkoutList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkQuantityIsRequired() throws Exception {
        int databaseSizeBeforeTest = checkoutRepository.findAll().collectList().block().size();
        // set the field null
        checkout.setQuantity(null);

        // Create the Checkout, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(checkout))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Checkout> checkoutList = checkoutRepository.findAll().collectList().block();
        assertThat(checkoutList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkTotalPriceIsRequired() throws Exception {
        int databaseSizeBeforeTest = checkoutRepository.findAll().collectList().block().size();
        // set the field null
        checkout.setTotalPrice(null);

        // Create the Checkout, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(checkout))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Checkout> checkoutList = checkoutRepository.findAll().collectList().block();
        assertThat(checkoutList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllCheckoutsAsStream() {
        // Initialize the database
        checkoutRepository.save(checkout).block();

        List<Checkout> checkoutList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Checkout.class)
            .getResponseBody()
            .filter(checkout::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(checkoutList).isNotNull();
        assertThat(checkoutList).hasSize(1);
        Checkout testCheckout = checkoutList.get(0);
        assertThat(testCheckout.getQuantity()).isEqualTo(DEFAULT_QUANTITY);
        assertThat(testCheckout.getTotalPrice()).isEqualByComparingTo(DEFAULT_TOTAL_PRICE);
    }

    @Test
    void getAllCheckouts() {
        // Initialize the database
        checkoutRepository.save(checkout).block();

        // Get all the checkoutList
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
            .value(hasItem(checkout.getId().intValue()))
            .jsonPath("$.[*].quantity")
            .value(hasItem(DEFAULT_QUANTITY))
            .jsonPath("$.[*].totalPrice")
            .value(hasItem(sameNumber(DEFAULT_TOTAL_PRICE)));
    }

    @Test
    void getCheckout() {
        // Initialize the database
        checkoutRepository.save(checkout).block();

        // Get the checkout
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, checkout.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(checkout.getId().intValue()))
            .jsonPath("$.quantity")
            .value(is(DEFAULT_QUANTITY))
            .jsonPath("$.totalPrice")
            .value(is(sameNumber(DEFAULT_TOTAL_PRICE)));
    }

    @Test
    void getNonExistingCheckout() {
        // Get the checkout
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewCheckout() throws Exception {
        // Initialize the database
        checkoutRepository.save(checkout).block();

        int databaseSizeBeforeUpdate = checkoutRepository.findAll().collectList().block().size();

        // Update the checkout
        Checkout updatedCheckout = checkoutRepository.findById(checkout.getId()).block();
        updatedCheckout.quantity(UPDATED_QUANTITY).totalPrice(UPDATED_TOTAL_PRICE);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedCheckout.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedCheckout))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Checkout in the database
        List<Checkout> checkoutList = checkoutRepository.findAll().collectList().block();
        assertThat(checkoutList).hasSize(databaseSizeBeforeUpdate);
        Checkout testCheckout = checkoutList.get(checkoutList.size() - 1);
        assertThat(testCheckout.getQuantity()).isEqualTo(UPDATED_QUANTITY);
        assertThat(testCheckout.getTotalPrice()).isEqualTo(UPDATED_TOTAL_PRICE);
    }

    @Test
    void putNonExistingCheckout() throws Exception {
        int databaseSizeBeforeUpdate = checkoutRepository.findAll().collectList().block().size();
        checkout.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, checkout.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(checkout))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Checkout in the database
        List<Checkout> checkoutList = checkoutRepository.findAll().collectList().block();
        assertThat(checkoutList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchCheckout() throws Exception {
        int databaseSizeBeforeUpdate = checkoutRepository.findAll().collectList().block().size();
        checkout.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(checkout))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Checkout in the database
        List<Checkout> checkoutList = checkoutRepository.findAll().collectList().block();
        assertThat(checkoutList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamCheckout() throws Exception {
        int databaseSizeBeforeUpdate = checkoutRepository.findAll().collectList().block().size();
        checkout.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(checkout))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Checkout in the database
        List<Checkout> checkoutList = checkoutRepository.findAll().collectList().block();
        assertThat(checkoutList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateCheckoutWithPatch() throws Exception {
        // Initialize the database
        checkoutRepository.save(checkout).block();

        int databaseSizeBeforeUpdate = checkoutRepository.findAll().collectList().block().size();

        // Update the checkout using partial update
        Checkout partialUpdatedCheckout = new Checkout();
        partialUpdatedCheckout.setId(checkout.getId());

        partialUpdatedCheckout.quantity(UPDATED_QUANTITY).totalPrice(UPDATED_TOTAL_PRICE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCheckout.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCheckout))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Checkout in the database
        List<Checkout> checkoutList = checkoutRepository.findAll().collectList().block();
        assertThat(checkoutList).hasSize(databaseSizeBeforeUpdate);
        Checkout testCheckout = checkoutList.get(checkoutList.size() - 1);
        assertThat(testCheckout.getQuantity()).isEqualTo(UPDATED_QUANTITY);
        assertThat(testCheckout.getTotalPrice()).isEqualByComparingTo(UPDATED_TOTAL_PRICE);
    }

    @Test
    void fullUpdateCheckoutWithPatch() throws Exception {
        // Initialize the database
        checkoutRepository.save(checkout).block();

        int databaseSizeBeforeUpdate = checkoutRepository.findAll().collectList().block().size();

        // Update the checkout using partial update
        Checkout partialUpdatedCheckout = new Checkout();
        partialUpdatedCheckout.setId(checkout.getId());

        partialUpdatedCheckout.quantity(UPDATED_QUANTITY).totalPrice(UPDATED_TOTAL_PRICE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCheckout.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCheckout))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Checkout in the database
        List<Checkout> checkoutList = checkoutRepository.findAll().collectList().block();
        assertThat(checkoutList).hasSize(databaseSizeBeforeUpdate);
        Checkout testCheckout = checkoutList.get(checkoutList.size() - 1);
        assertThat(testCheckout.getQuantity()).isEqualTo(UPDATED_QUANTITY);
        assertThat(testCheckout.getTotalPrice()).isEqualByComparingTo(UPDATED_TOTAL_PRICE);
    }

    @Test
    void patchNonExistingCheckout() throws Exception {
        int databaseSizeBeforeUpdate = checkoutRepository.findAll().collectList().block().size();
        checkout.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, checkout.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(checkout))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Checkout in the database
        List<Checkout> checkoutList = checkoutRepository.findAll().collectList().block();
        assertThat(checkoutList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchCheckout() throws Exception {
        int databaseSizeBeforeUpdate = checkoutRepository.findAll().collectList().block().size();
        checkout.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(checkout))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Checkout in the database
        List<Checkout> checkoutList = checkoutRepository.findAll().collectList().block();
        assertThat(checkoutList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamCheckout() throws Exception {
        int databaseSizeBeforeUpdate = checkoutRepository.findAll().collectList().block().size();
        checkout.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(checkout))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Checkout in the database
        List<Checkout> checkoutList = checkoutRepository.findAll().collectList().block();
        assertThat(checkoutList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteCheckout() {
        // Initialize the database
        checkoutRepository.save(checkout).block();

        int databaseSizeBeforeDelete = checkoutRepository.findAll().collectList().block().size();

        // Delete the checkout
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, checkout.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Checkout> checkoutList = checkoutRepository.findAll().collectList().block();
        assertThat(checkoutList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
