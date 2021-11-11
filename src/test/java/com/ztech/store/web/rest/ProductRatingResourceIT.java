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
 * Integration tests for the {@link ProductRatingResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class ProductRatingResourceIT {

    private static final Float DEFAULT_RATING = 0F;
    private static final Float UPDATED_RATING = 1F;

    private static final Instant DEFAULT_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

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
        ProductRating productRating = new ProductRating().rating(DEFAULT_RATING).date(DEFAULT_DATE);
        // Add required entity
        User user = em.insert(UserResourceIT.createEntity(em)).block();
        productRating.setUser(user);
        // Add required entity
        Product product;
        product = em.insert(ProductResourceIT.createEntity(em)).block();
        productRating.setProduct(product);
        return productRating;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProductRating createUpdatedEntity(EntityManager em) {
        ProductRating productRating = new ProductRating().rating(UPDATED_RATING).date(UPDATED_DATE);
        // Add required entity
        User user = em.insert(UserResourceIT.createEntity(em)).block();
        productRating.setUser(user);
        // Add required entity
        Product product;
        product = em.insert(ProductResourceIT.createUpdatedEntity(em)).block();
        productRating.setProduct(product);
        return productRating;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(ProductRating.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        UserResourceIT.deleteEntities(em);
        ProductResourceIT.deleteEntities(em);
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
        assertThat(testProductRating.getRating()).isEqualTo(DEFAULT_RATING);
        assertThat(testProductRating.getDate()).isEqualTo(DEFAULT_DATE);
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
    void checkRatingIsRequired() throws Exception {
        int databaseSizeBeforeTest = productRatingRepository.findAll().collectList().block().size();
        // set the field null
        productRating.setRating(null);

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
    void checkDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = productRatingRepository.findAll().collectList().block().size();
        // set the field null
        productRating.setDate(null);

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
        assertThat(testProductRating.getRating()).isEqualTo(DEFAULT_RATING);
        assertThat(testProductRating.getDate()).isEqualTo(DEFAULT_DATE);
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
            .jsonPath("$.[*].rating")
            .value(hasItem(DEFAULT_RATING.doubleValue()))
            .jsonPath("$.[*].date")
            .value(hasItem(DEFAULT_DATE.toString()));
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
            .jsonPath("$.rating")
            .value(is(DEFAULT_RATING.doubleValue()))
            .jsonPath("$.date")
            .value(is(DEFAULT_DATE.toString()));
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
        updatedProductRating.rating(UPDATED_RATING).date(UPDATED_DATE);

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
        assertThat(testProductRating.getRating()).isEqualTo(UPDATED_RATING);
        assertThat(testProductRating.getDate()).isEqualTo(UPDATED_DATE);
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

        partialUpdatedProductRating.rating(UPDATED_RATING);

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
        assertThat(testProductRating.getRating()).isEqualTo(UPDATED_RATING);
        assertThat(testProductRating.getDate()).isEqualTo(DEFAULT_DATE);
    }

    @Test
    void fullUpdateProductRatingWithPatch() throws Exception {
        // Initialize the database
        productRatingRepository.save(productRating).block();

        int databaseSizeBeforeUpdate = productRatingRepository.findAll().collectList().block().size();

        // Update the productRating using partial update
        ProductRating partialUpdatedProductRating = new ProductRating();
        partialUpdatedProductRating.setId(productRating.getId());

        partialUpdatedProductRating.rating(UPDATED_RATING).date(UPDATED_DATE);

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
        assertThat(testProductRating.getRating()).isEqualTo(UPDATED_RATING);
        assertThat(testProductRating.getDate()).isEqualTo(UPDATED_DATE);
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
