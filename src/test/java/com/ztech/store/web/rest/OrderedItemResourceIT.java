package com.ztech.store.web.rest;

import static com.ztech.store.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.ztech.store.IntegrationTest;
import com.ztech.store.domain.OrderedItem;
import com.ztech.store.domain.ProductOrder;
import com.ztech.store.domain.enumeration.OrderedItemStatus;
import com.ztech.store.repository.OrderedItemRepository;
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
 * Integration tests for the {@link OrderedItemResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class OrderedItemResourceIT {

    private static final Integer DEFAULT_QUANTITY = 0;
    private static final Integer UPDATED_QUANTITY = 1;

    private static final BigDecimal DEFAULT_TOTAL_PRICE = new BigDecimal(0);
    private static final BigDecimal UPDATED_TOTAL_PRICE = new BigDecimal(1);

    private static final OrderedItemStatus DEFAULT_STATUS = OrderedItemStatus.AVAILABLE;
    private static final OrderedItemStatus UPDATED_STATUS = OrderedItemStatus.OUT_OF_STOCK;

    private static final String ENTITY_API_URL = "/api/ordered-items";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private OrderedItemRepository orderedItemRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private OrderedItem orderedItem;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OrderedItem createEntity(EntityManager em) {
        OrderedItem orderedItem = new OrderedItem().quantity(DEFAULT_QUANTITY).totalPrice(DEFAULT_TOTAL_PRICE).status(DEFAULT_STATUS);
        // Add required entity
        ProductOrder productOrder;
        productOrder = em.insert(ProductOrderResourceIT.createEntity(em)).block();
        orderedItem.setOrder(productOrder);
        return orderedItem;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static OrderedItem createUpdatedEntity(EntityManager em) {
        OrderedItem orderedItem = new OrderedItem().quantity(UPDATED_QUANTITY).totalPrice(UPDATED_TOTAL_PRICE).status(UPDATED_STATUS);
        // Add required entity
        ProductOrder productOrder;
        productOrder = em.insert(ProductOrderResourceIT.createUpdatedEntity(em)).block();
        orderedItem.setOrder(productOrder);
        return orderedItem;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(OrderedItem.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        ProductOrderResourceIT.deleteEntities(em);
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        orderedItem = createEntity(em);
    }

    @Test
    void createOrderedItem() throws Exception {
        int databaseSizeBeforeCreate = orderedItemRepository.findAll().collectList().block().size();
        // Create the OrderedItem
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(orderedItem))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the OrderedItem in the database
        List<OrderedItem> orderedItemList = orderedItemRepository.findAll().collectList().block();
        assertThat(orderedItemList).hasSize(databaseSizeBeforeCreate + 1);
        OrderedItem testOrderedItem = orderedItemList.get(orderedItemList.size() - 1);
        assertThat(testOrderedItem.getQuantity()).isEqualTo(DEFAULT_QUANTITY);
        assertThat(testOrderedItem.getTotalPrice()).isEqualByComparingTo(DEFAULT_TOTAL_PRICE);
        assertThat(testOrderedItem.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    void createOrderedItemWithExistingId() throws Exception {
        // Create the OrderedItem with an existing ID
        orderedItem.setId(1L);

        int databaseSizeBeforeCreate = orderedItemRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(orderedItem))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the OrderedItem in the database
        List<OrderedItem> orderedItemList = orderedItemRepository.findAll().collectList().block();
        assertThat(orderedItemList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkQuantityIsRequired() throws Exception {
        int databaseSizeBeforeTest = orderedItemRepository.findAll().collectList().block().size();
        // set the field null
        orderedItem.setQuantity(null);

        // Create the OrderedItem, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(orderedItem))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<OrderedItem> orderedItemList = orderedItemRepository.findAll().collectList().block();
        assertThat(orderedItemList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkTotalPriceIsRequired() throws Exception {
        int databaseSizeBeforeTest = orderedItemRepository.findAll().collectList().block().size();
        // set the field null
        orderedItem.setTotalPrice(null);

        // Create the OrderedItem, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(orderedItem))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<OrderedItem> orderedItemList = orderedItemRepository.findAll().collectList().block();
        assertThat(orderedItemList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkStatusIsRequired() throws Exception {
        int databaseSizeBeforeTest = orderedItemRepository.findAll().collectList().block().size();
        // set the field null
        orderedItem.setStatus(null);

        // Create the OrderedItem, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(orderedItem))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<OrderedItem> orderedItemList = orderedItemRepository.findAll().collectList().block();
        assertThat(orderedItemList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllOrderedItems() {
        // Initialize the database
        orderedItemRepository.save(orderedItem).block();

        // Get all the orderedItemList
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
            .value(hasItem(orderedItem.getId().intValue()))
            .jsonPath("$.[*].quantity")
            .value(hasItem(DEFAULT_QUANTITY))
            .jsonPath("$.[*].totalPrice")
            .value(hasItem(sameNumber(DEFAULT_TOTAL_PRICE)))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()));
    }

    @Test
    void getOrderedItem() {
        // Initialize the database
        orderedItemRepository.save(orderedItem).block();

        // Get the orderedItem
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, orderedItem.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(orderedItem.getId().intValue()))
            .jsonPath("$.quantity")
            .value(is(DEFAULT_QUANTITY))
            .jsonPath("$.totalPrice")
            .value(is(sameNumber(DEFAULT_TOTAL_PRICE)))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS.toString()));
    }

    @Test
    void getNonExistingOrderedItem() {
        // Get the orderedItem
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewOrderedItem() throws Exception {
        // Initialize the database
        orderedItemRepository.save(orderedItem).block();

        int databaseSizeBeforeUpdate = orderedItemRepository.findAll().collectList().block().size();

        // Update the orderedItem
        OrderedItem updatedOrderedItem = orderedItemRepository.findById(orderedItem.getId()).block();
        updatedOrderedItem.quantity(UPDATED_QUANTITY).totalPrice(UPDATED_TOTAL_PRICE).status(UPDATED_STATUS);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedOrderedItem.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedOrderedItem))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the OrderedItem in the database
        List<OrderedItem> orderedItemList = orderedItemRepository.findAll().collectList().block();
        assertThat(orderedItemList).hasSize(databaseSizeBeforeUpdate);
        OrderedItem testOrderedItem = orderedItemList.get(orderedItemList.size() - 1);
        assertThat(testOrderedItem.getQuantity()).isEqualTo(UPDATED_QUANTITY);
        assertThat(testOrderedItem.getTotalPrice()).isEqualTo(UPDATED_TOTAL_PRICE);
        assertThat(testOrderedItem.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    void putNonExistingOrderedItem() throws Exception {
        int databaseSizeBeforeUpdate = orderedItemRepository.findAll().collectList().block().size();
        orderedItem.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, orderedItem.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(orderedItem))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the OrderedItem in the database
        List<OrderedItem> orderedItemList = orderedItemRepository.findAll().collectList().block();
        assertThat(orderedItemList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchOrderedItem() throws Exception {
        int databaseSizeBeforeUpdate = orderedItemRepository.findAll().collectList().block().size();
        orderedItem.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(orderedItem))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the OrderedItem in the database
        List<OrderedItem> orderedItemList = orderedItemRepository.findAll().collectList().block();
        assertThat(orderedItemList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamOrderedItem() throws Exception {
        int databaseSizeBeforeUpdate = orderedItemRepository.findAll().collectList().block().size();
        orderedItem.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(orderedItem))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the OrderedItem in the database
        List<OrderedItem> orderedItemList = orderedItemRepository.findAll().collectList().block();
        assertThat(orderedItemList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateOrderedItemWithPatch() throws Exception {
        // Initialize the database
        orderedItemRepository.save(orderedItem).block();

        int databaseSizeBeforeUpdate = orderedItemRepository.findAll().collectList().block().size();

        // Update the orderedItem using partial update
        OrderedItem partialUpdatedOrderedItem = new OrderedItem();
        partialUpdatedOrderedItem.setId(orderedItem.getId());

        partialUpdatedOrderedItem.quantity(UPDATED_QUANTITY);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedOrderedItem.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedOrderedItem))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the OrderedItem in the database
        List<OrderedItem> orderedItemList = orderedItemRepository.findAll().collectList().block();
        assertThat(orderedItemList).hasSize(databaseSizeBeforeUpdate);
        OrderedItem testOrderedItem = orderedItemList.get(orderedItemList.size() - 1);
        assertThat(testOrderedItem.getQuantity()).isEqualTo(UPDATED_QUANTITY);
        assertThat(testOrderedItem.getTotalPrice()).isEqualByComparingTo(DEFAULT_TOTAL_PRICE);
        assertThat(testOrderedItem.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    void fullUpdateOrderedItemWithPatch() throws Exception {
        // Initialize the database
        orderedItemRepository.save(orderedItem).block();

        int databaseSizeBeforeUpdate = orderedItemRepository.findAll().collectList().block().size();

        // Update the orderedItem using partial update
        OrderedItem partialUpdatedOrderedItem = new OrderedItem();
        partialUpdatedOrderedItem.setId(orderedItem.getId());

        partialUpdatedOrderedItem.quantity(UPDATED_QUANTITY).totalPrice(UPDATED_TOTAL_PRICE).status(UPDATED_STATUS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedOrderedItem.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedOrderedItem))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the OrderedItem in the database
        List<OrderedItem> orderedItemList = orderedItemRepository.findAll().collectList().block();
        assertThat(orderedItemList).hasSize(databaseSizeBeforeUpdate);
        OrderedItem testOrderedItem = orderedItemList.get(orderedItemList.size() - 1);
        assertThat(testOrderedItem.getQuantity()).isEqualTo(UPDATED_QUANTITY);
        assertThat(testOrderedItem.getTotalPrice()).isEqualByComparingTo(UPDATED_TOTAL_PRICE);
        assertThat(testOrderedItem.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    void patchNonExistingOrderedItem() throws Exception {
        int databaseSizeBeforeUpdate = orderedItemRepository.findAll().collectList().block().size();
        orderedItem.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, orderedItem.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(orderedItem))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the OrderedItem in the database
        List<OrderedItem> orderedItemList = orderedItemRepository.findAll().collectList().block();
        assertThat(orderedItemList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchOrderedItem() throws Exception {
        int databaseSizeBeforeUpdate = orderedItemRepository.findAll().collectList().block().size();
        orderedItem.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(orderedItem))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the OrderedItem in the database
        List<OrderedItem> orderedItemList = orderedItemRepository.findAll().collectList().block();
        assertThat(orderedItemList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamOrderedItem() throws Exception {
        int databaseSizeBeforeUpdate = orderedItemRepository.findAll().collectList().block().size();
        orderedItem.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(orderedItem))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the OrderedItem in the database
        List<OrderedItem> orderedItemList = orderedItemRepository.findAll().collectList().block();
        assertThat(orderedItemList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteOrderedItem() {
        // Initialize the database
        orderedItemRepository.save(orderedItem).block();

        int databaseSizeBeforeDelete = orderedItemRepository.findAll().collectList().block().size();

        // Delete the orderedItem
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, orderedItem.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<OrderedItem> orderedItemList = orderedItemRepository.findAll().collectList().block();
        assertThat(orderedItemList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
