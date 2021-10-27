package com.ztech.store.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.ztech.store.IntegrationTest;
import com.ztech.store.domain.Product;
import com.ztech.store.domain.ProductRating;
import com.ztech.store.domain.User;
import com.ztech.store.repository.ProductRatingRepository;
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
 * Integration tests for the {@link ProductRatingResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class ProductRatingResourceIT {

    private static final Float DEFAULT_VALUE = 0F;
    private static final Float UPDATED_VALUE = 1F;

    private static final Long DEFAULT_PRODUCT_ID = 1L;
    private static final Long UPDATED_PRODUCT_ID = 2L;

    private static final Long DEFAULT_USER_ID = 1L;
    private static final Long UPDATED_USER_ID = 2L;

    private static final String ENTITY_API_URL = "/api/product-ratings";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ProductRatingRepository productRatingRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private ProductRating productRating;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProductRating createEntity(EntityManager em) {
        ProductRating productRating = new ProductRating().value(DEFAULT_VALUE).productId(DEFAULT_PRODUCT_ID).userId(DEFAULT_USER_ID);
        // Add required entity
        Product product;
        product = em.insert(ProductResourceIT.createEntity(em)).block();
        productRating.setProductRating(product);
        // Add required entity
        User user = em.insert(UserResourceIT.createEntity(em)).block();
        productRating.setRating(user);
        return productRating;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProductRating createUpdatedEntity(EntityManager em) {
        ProductRating productRating = new ProductRating().value(UPDATED_VALUE).productId(UPDATED_PRODUCT_ID).userId(UPDATED_USER_ID);
        // Add required entity
        Product product;
        product = em.insert(ProductResourceIT.createUpdatedEntity(em)).block();
        productRating.setProductRating(product);
        // Add required entity
        User user = em.insert(UserResourceIT.createEntity(em)).block();
        productRating.setRating(user);
        return productRating;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(ProductRating.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        ProductResourceIT.deleteEntities(em);
        UserResourceIT.deleteEntities(em);
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        productRating = createEntity(em);
    }

    @Test
    void createProductRating() throws Exception {
        int databaseSizeBeforeCreate = productRatingRepository.findAll().collectList().block().size();
        // Create the ProductRating
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productRating))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the ProductRating in the database
        List<ProductRating> productRatingList = productRatingRepository.findAll().collectList().block();
        assertThat(productRatingList).hasSize(databaseSizeBeforeCreate + 1);
        ProductRating testProductRating = productRatingList.get(productRatingList.size() - 1);
        assertThat(testProductRating.getValue()).isEqualTo(DEFAULT_VALUE);
        assertThat(testProductRating.getProductId()).isEqualTo(DEFAULT_PRODUCT_ID);
        assertThat(testProductRating.getUserId()).isEqualTo(DEFAULT_USER_ID);
    }

    @Test
    void createProductRatingWithExistingId() throws Exception {
        // Create the ProductRating with an existing ID
        productRating.setId(1L);

        int databaseSizeBeforeCreate = productRatingRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productRating))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ProductRating in the database
        List<ProductRating> productRatingList = productRatingRepository.findAll().collectList().block();
        assertThat(productRatingList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkValueIsRequired() throws Exception {
        int databaseSizeBeforeTest = productRatingRepository.findAll().collectList().block().size();
        // set the field null
        productRating.setValue(null);

        // Create the ProductRating, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productRating))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<ProductRating> productRatingList = productRatingRepository.findAll().collectList().block();
        assertThat(productRatingList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkProductIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = productRatingRepository.findAll().collectList().block().size();
        // set the field null
        productRating.setProductId(null);

        // Create the ProductRating, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productRating))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<ProductRating> productRatingList = productRatingRepository.findAll().collectList().block();
        assertThat(productRatingList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkUserIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = productRatingRepository.findAll().collectList().block().size();
        // set the field null
        productRating.setUserId(null);

        // Create the ProductRating, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productRating))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<ProductRating> productRatingList = productRatingRepository.findAll().collectList().block();
        assertThat(productRatingList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllProductRatingsAsStream() {
        // Initialize the database
        productRatingRepository.save(productRating).block();

        List<ProductRating> productRatingList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(ProductRating.class)
            .getResponseBody()
            .filter(productRating::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(productRatingList).isNotNull();
        assertThat(productRatingList).hasSize(1);
        ProductRating testProductRating = productRatingList.get(0);
        assertThat(testProductRating.getValue()).isEqualTo(DEFAULT_VALUE);
        assertThat(testProductRating.getProductId()).isEqualTo(DEFAULT_PRODUCT_ID);
        assertThat(testProductRating.getUserId()).isEqualTo(DEFAULT_USER_ID);
    }

    @Test
    void getAllProductRatings() {
        // Initialize the database
        productRatingRepository.save(productRating).block();

        // Get all the productRatingList
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
            .value(hasItem(productRating.getId().intValue()))
            .jsonPath("$.[*].value")
            .value(hasItem(DEFAULT_VALUE.doubleValue()))
            .jsonPath("$.[*].productId")
            .value(hasItem(DEFAULT_PRODUCT_ID.intValue()))
            .jsonPath("$.[*].userId")
            .value(hasItem(DEFAULT_USER_ID.intValue()));
    }

    @Test
    void getProductRating() {
        // Initialize the database
        productRatingRepository.save(productRating).block();

        // Get the productRating
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, productRating.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(productRating.getId().intValue()))
            .jsonPath("$.value")
            .value(is(DEFAULT_VALUE.doubleValue()))
            .jsonPath("$.productId")
            .value(is(DEFAULT_PRODUCT_ID.intValue()))
            .jsonPath("$.userId")
            .value(is(DEFAULT_USER_ID.intValue()));
    }

    @Test
    void getNonExistingProductRating() {
        // Get the productRating
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewProductRating() throws Exception {
        // Initialize the database
        productRatingRepository.save(productRating).block();

        int databaseSizeBeforeUpdate = productRatingRepository.findAll().collectList().block().size();

        // Update the productRating
        ProductRating updatedProductRating = productRatingRepository.findById(productRating.getId()).block();
        updatedProductRating.value(UPDATED_VALUE).productId(UPDATED_PRODUCT_ID).userId(UPDATED_USER_ID);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedProductRating.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedProductRating))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ProductRating in the database
        List<ProductRating> productRatingList = productRatingRepository.findAll().collectList().block();
        assertThat(productRatingList).hasSize(databaseSizeBeforeUpdate);
        ProductRating testProductRating = productRatingList.get(productRatingList.size() - 1);
        assertThat(testProductRating.getValue()).isEqualTo(UPDATED_VALUE);
        assertThat(testProductRating.getProductId()).isEqualTo(UPDATED_PRODUCT_ID);
        assertThat(testProductRating.getUserId()).isEqualTo(UPDATED_USER_ID);
    }

    @Test
    void putNonExistingProductRating() throws Exception {
        int databaseSizeBeforeUpdate = productRatingRepository.findAll().collectList().block().size();
        productRating.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, productRating.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productRating))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ProductRating in the database
        List<ProductRating> productRatingList = productRatingRepository.findAll().collectList().block();
        assertThat(productRatingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchProductRating() throws Exception {
        int databaseSizeBeforeUpdate = productRatingRepository.findAll().collectList().block().size();
        productRating.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productRating))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ProductRating in the database
        List<ProductRating> productRatingList = productRatingRepository.findAll().collectList().block();
        assertThat(productRatingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamProductRating() throws Exception {
        int databaseSizeBeforeUpdate = productRatingRepository.findAll().collectList().block().size();
        productRating.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(productRating))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ProductRating in the database
        List<ProductRating> productRatingList = productRatingRepository.findAll().collectList().block();
        assertThat(productRatingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateProductRatingWithPatch() throws Exception {
        // Initialize the database
        productRatingRepository.save(productRating).block();

        int databaseSizeBeforeUpdate = productRatingRepository.findAll().collectList().block().size();

        // Update the productRating using partial update
        ProductRating partialUpdatedProductRating = new ProductRating();
        partialUpdatedProductRating.setId(productRating.getId());

        partialUpdatedProductRating.value(UPDATED_VALUE).userId(UPDATED_USER_ID);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedProductRating.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedProductRating))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ProductRating in the database
        List<ProductRating> productRatingList = productRatingRepository.findAll().collectList().block();
        assertThat(productRatingList).hasSize(databaseSizeBeforeUpdate);
        ProductRating testProductRating = productRatingList.get(productRatingList.size() - 1);
        assertThat(testProductRating.getValue()).isEqualTo(UPDATED_VALUE);
        assertThat(testProductRating.getProductId()).isEqualTo(DEFAULT_PRODUCT_ID);
        assertThat(testProductRating.getUserId()).isEqualTo(UPDATED_USER_ID);
    }

    @Test
    void fullUpdateProductRatingWithPatch() throws Exception {
        // Initialize the database
        productRatingRepository.save(productRating).block();

        int databaseSizeBeforeUpdate = productRatingRepository.findAll().collectList().block().size();

        // Update the productRating using partial update
        ProductRating partialUpdatedProductRating = new ProductRating();
        partialUpdatedProductRating.setId(productRating.getId());

        partialUpdatedProductRating.value(UPDATED_VALUE).productId(UPDATED_PRODUCT_ID).userId(UPDATED_USER_ID);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedProductRating.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedProductRating))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ProductRating in the database
        List<ProductRating> productRatingList = productRatingRepository.findAll().collectList().block();
        assertThat(productRatingList).hasSize(databaseSizeBeforeUpdate);
        ProductRating testProductRating = productRatingList.get(productRatingList.size() - 1);
        assertThat(testProductRating.getValue()).isEqualTo(UPDATED_VALUE);
        assertThat(testProductRating.getProductId()).isEqualTo(UPDATED_PRODUCT_ID);
        assertThat(testProductRating.getUserId()).isEqualTo(UPDATED_USER_ID);
    }

    @Test
    void patchNonExistingProductRating() throws Exception {
        int databaseSizeBeforeUpdate = productRatingRepository.findAll().collectList().block().size();
        productRating.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, productRating.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(productRating))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ProductRating in the database
        List<ProductRating> productRatingList = productRatingRepository.findAll().collectList().block();
        assertThat(productRatingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchProductRating() throws Exception {
        int databaseSizeBeforeUpdate = productRatingRepository.findAll().collectList().block().size();
        productRating.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(productRating))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ProductRating in the database
        List<ProductRating> productRatingList = productRatingRepository.findAll().collectList().block();
        assertThat(productRatingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamProductRating() throws Exception {
        int databaseSizeBeforeUpdate = productRatingRepository.findAll().collectList().block().size();
        productRating.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(productRating))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ProductRating in the database
        List<ProductRating> productRatingList = productRatingRepository.findAll().collectList().block();
        assertThat(productRatingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteProductRating() {
        // Initialize the database
        productRatingRepository.save(productRating).block();

        int databaseSizeBeforeDelete = productRatingRepository.findAll().collectList().block().size();

        // Delete the productRating
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, productRating.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<ProductRating> productRatingList = productRatingRepository.findAll().collectList().block();
        assertThat(productRatingList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
