package com.ztech.store.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.ztech.store.IntegrationTest;
import com.ztech.store.domain.User;
import com.ztech.store.domain.Vendor;
import com.ztech.store.domain.enumeration.Gender;
import com.ztech.store.repository.VendorRepository;
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
 * Integration tests for the {@link VendorResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class VendorResourceIT {

    private static final String DEFAULT_FIRST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LAST_NAME = "BBBBBBBBBB";

    private static final Gender DEFAULT_GENDER = Gender.MALE;
    private static final Gender UPDATED_GENDER = Gender.FEMALE;

    private static final String DEFAULT_EMAIL = "OpMU@\\.u4b1";
    private static final String UPDATED_EMAIL = "I8BJ@ASId.MRU";

    private static final String DEFAULT_PHONE = "AAAAAAAAAA";
    private static final String UPDATED_PHONE = "BBBBBBBBBB";

    private static final String DEFAULT_PHONE_2 = "AAAAAAAAAA";
    private static final String UPDATED_PHONE_2 = "BBBBBBBBBB";

    private static final String DEFAULT_ADDRESS_LINE_1 = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS_LINE_1 = "BBBBBBBBBB";

    private static final String DEFAULT_ADDRESS_LINE_2 = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS_LINE_2 = "BBBBBBBBBB";

    private static final String DEFAULT_CITY = "AAAAAAAAAA";
    private static final String UPDATED_CITY = "BBBBBBBBBB";

    private static final String DEFAULT_COUNTRY = "AAAAAAAAAA";
    private static final String UPDATED_COUNTRY = "BBBBBBBBBB";

    private static final String DEFAULT_STORE_NAME = "AAAAAAAAAA";
    private static final String UPDATED_STORE_NAME = "BBBBBBBBBB";

    private static final Long DEFAULT_BUSINESS_ACCOUNT_NUMBER = 1L;
    private static final Long UPDATED_BUSINESS_ACCOUNT_NUMBER = 2L;

    private static final String ENTITY_API_URL = "/api/vendors";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Vendor vendor;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Vendor createEntity(EntityManager em) {
        Vendor vendor = new Vendor()
            .firstName(DEFAULT_FIRST_NAME)
            .lastName(DEFAULT_LAST_NAME)
            .gender(DEFAULT_GENDER)
            .email(DEFAULT_EMAIL)
            .phone(DEFAULT_PHONE)
            .phone2(DEFAULT_PHONE_2)
            .addressLine1(DEFAULT_ADDRESS_LINE_1)
            .addressLine2(DEFAULT_ADDRESS_LINE_2)
            .city(DEFAULT_CITY)
            .country(DEFAULT_COUNTRY)
            .storeName(DEFAULT_STORE_NAME)
            .businessAccountNumber(DEFAULT_BUSINESS_ACCOUNT_NUMBER);
        // Add required entity
        User user = em.insert(UserResourceIT.createEntity(em)).block();
        vendor.setUser(user);
        return vendor;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Vendor createUpdatedEntity(EntityManager em) {
        Vendor vendor = new Vendor()
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .gender(UPDATED_GENDER)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE)
            .phone2(UPDATED_PHONE_2)
            .addressLine1(UPDATED_ADDRESS_LINE_1)
            .addressLine2(UPDATED_ADDRESS_LINE_2)
            .city(UPDATED_CITY)
            .country(UPDATED_COUNTRY)
            .storeName(UPDATED_STORE_NAME)
            .businessAccountNumber(UPDATED_BUSINESS_ACCOUNT_NUMBER);
        // Add required entity
        User user = em.insert(UserResourceIT.createEntity(em)).block();
        vendor.setUser(user);
        return vendor;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Vendor.class).block();
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
        vendor = createEntity(em);
    }

    @Test
    void createVendor() throws Exception {
        int databaseSizeBeforeCreate = vendorRepository.findAll().collectList().block().size();
        // Create the Vendor
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(vendor))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Vendor in the database
        List<Vendor> vendorList = vendorRepository.findAll().collectList().block();
        assertThat(vendorList).hasSize(databaseSizeBeforeCreate + 1);
        Vendor testVendor = vendorList.get(vendorList.size() - 1);
        assertThat(testVendor.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testVendor.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
        assertThat(testVendor.getGender()).isEqualTo(DEFAULT_GENDER);
        assertThat(testVendor.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testVendor.getPhone()).isEqualTo(DEFAULT_PHONE);
        assertThat(testVendor.getPhone2()).isEqualTo(DEFAULT_PHONE_2);
        assertThat(testVendor.getAddressLine1()).isEqualTo(DEFAULT_ADDRESS_LINE_1);
        assertThat(testVendor.getAddressLine2()).isEqualTo(DEFAULT_ADDRESS_LINE_2);
        assertThat(testVendor.getCity()).isEqualTo(DEFAULT_CITY);
        assertThat(testVendor.getCountry()).isEqualTo(DEFAULT_COUNTRY);
        assertThat(testVendor.getStoreName()).isEqualTo(DEFAULT_STORE_NAME);
        assertThat(testVendor.getBusinessAccountNumber()).isEqualTo(DEFAULT_BUSINESS_ACCOUNT_NUMBER);
    }

    @Test
    void createVendorWithExistingId() throws Exception {
        // Create the Vendor with an existing ID
        vendor.setId(1L);

        int databaseSizeBeforeCreate = vendorRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(vendor))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Vendor in the database
        List<Vendor> vendorList = vendorRepository.findAll().collectList().block();
        assertThat(vendorList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkFirstNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = vendorRepository.findAll().collectList().block().size();
        // set the field null
        vendor.setFirstName(null);

        // Create the Vendor, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(vendor))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Vendor> vendorList = vendorRepository.findAll().collectList().block();
        assertThat(vendorList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkLastNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = vendorRepository.findAll().collectList().block().size();
        // set the field null
        vendor.setLastName(null);

        // Create the Vendor, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(vendor))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Vendor> vendorList = vendorRepository.findAll().collectList().block();
        assertThat(vendorList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkGenderIsRequired() throws Exception {
        int databaseSizeBeforeTest = vendorRepository.findAll().collectList().block().size();
        // set the field null
        vendor.setGender(null);

        // Create the Vendor, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(vendor))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Vendor> vendorList = vendorRepository.findAll().collectList().block();
        assertThat(vendorList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkEmailIsRequired() throws Exception {
        int databaseSizeBeforeTest = vendorRepository.findAll().collectList().block().size();
        // set the field null
        vendor.setEmail(null);

        // Create the Vendor, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(vendor))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Vendor> vendorList = vendorRepository.findAll().collectList().block();
        assertThat(vendorList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkPhoneIsRequired() throws Exception {
        int databaseSizeBeforeTest = vendorRepository.findAll().collectList().block().size();
        // set the field null
        vendor.setPhone(null);

        // Create the Vendor, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(vendor))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Vendor> vendorList = vendorRepository.findAll().collectList().block();
        assertThat(vendorList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkAddressLine1IsRequired() throws Exception {
        int databaseSizeBeforeTest = vendorRepository.findAll().collectList().block().size();
        // set the field null
        vendor.setAddressLine1(null);

        // Create the Vendor, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(vendor))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Vendor> vendorList = vendorRepository.findAll().collectList().block();
        assertThat(vendorList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkCityIsRequired() throws Exception {
        int databaseSizeBeforeTest = vendorRepository.findAll().collectList().block().size();
        // set the field null
        vendor.setCity(null);

        // Create the Vendor, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(vendor))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Vendor> vendorList = vendorRepository.findAll().collectList().block();
        assertThat(vendorList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkCountryIsRequired() throws Exception {
        int databaseSizeBeforeTest = vendorRepository.findAll().collectList().block().size();
        // set the field null
        vendor.setCountry(null);

        // Create the Vendor, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(vendor))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Vendor> vendorList = vendorRepository.findAll().collectList().block();
        assertThat(vendorList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkStoreNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = vendorRepository.findAll().collectList().block().size();
        // set the field null
        vendor.setStoreName(null);

        // Create the Vendor, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(vendor))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Vendor> vendorList = vendorRepository.findAll().collectList().block();
        assertThat(vendorList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkBusinessAccountNumberIsRequired() throws Exception {
        int databaseSizeBeforeTest = vendorRepository.findAll().collectList().block().size();
        // set the field null
        vendor.setBusinessAccountNumber(null);

        // Create the Vendor, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(vendor))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Vendor> vendorList = vendorRepository.findAll().collectList().block();
        assertThat(vendorList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllVendorsAsStream() {
        // Initialize the database
        vendorRepository.save(vendor).block();

        List<Vendor> vendorList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Vendor.class)
            .getResponseBody()
            .filter(vendor::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(vendorList).isNotNull();
        assertThat(vendorList).hasSize(1);
        Vendor testVendor = vendorList.get(0);
        assertThat(testVendor.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testVendor.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
        assertThat(testVendor.getGender()).isEqualTo(DEFAULT_GENDER);
        assertThat(testVendor.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testVendor.getPhone()).isEqualTo(DEFAULT_PHONE);
        assertThat(testVendor.getPhone2()).isEqualTo(DEFAULT_PHONE_2);
        assertThat(testVendor.getAddressLine1()).isEqualTo(DEFAULT_ADDRESS_LINE_1);
        assertThat(testVendor.getAddressLine2()).isEqualTo(DEFAULT_ADDRESS_LINE_2);
        assertThat(testVendor.getCity()).isEqualTo(DEFAULT_CITY);
        assertThat(testVendor.getCountry()).isEqualTo(DEFAULT_COUNTRY);
        assertThat(testVendor.getStoreName()).isEqualTo(DEFAULT_STORE_NAME);
        assertThat(testVendor.getBusinessAccountNumber()).isEqualTo(DEFAULT_BUSINESS_ACCOUNT_NUMBER);
    }

    @Test
    void getAllVendors() {
        // Initialize the database
        vendorRepository.save(vendor).block();

        // Get all the vendorList
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
            .value(hasItem(vendor.getId().intValue()))
            .jsonPath("$.[*].firstName")
            .value(hasItem(DEFAULT_FIRST_NAME))
            .jsonPath("$.[*].lastName")
            .value(hasItem(DEFAULT_LAST_NAME))
            .jsonPath("$.[*].gender")
            .value(hasItem(DEFAULT_GENDER.toString()))
            .jsonPath("$.[*].email")
            .value(hasItem(DEFAULT_EMAIL))
            .jsonPath("$.[*].phone")
            .value(hasItem(DEFAULT_PHONE))
            .jsonPath("$.[*].phone2")
            .value(hasItem(DEFAULT_PHONE_2))
            .jsonPath("$.[*].addressLine1")
            .value(hasItem(DEFAULT_ADDRESS_LINE_1))
            .jsonPath("$.[*].addressLine2")
            .value(hasItem(DEFAULT_ADDRESS_LINE_2))
            .jsonPath("$.[*].city")
            .value(hasItem(DEFAULT_CITY))
            .jsonPath("$.[*].country")
            .value(hasItem(DEFAULT_COUNTRY))
            .jsonPath("$.[*].storeName")
            .value(hasItem(DEFAULT_STORE_NAME))
            .jsonPath("$.[*].businessAccountNumber")
            .value(hasItem(DEFAULT_BUSINESS_ACCOUNT_NUMBER.intValue()));
    }

    @Test
    void getVendor() {
        // Initialize the database
        vendorRepository.save(vendor).block();

        // Get the vendor
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, vendor.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(vendor.getId().intValue()))
            .jsonPath("$.firstName")
            .value(is(DEFAULT_FIRST_NAME))
            .jsonPath("$.lastName")
            .value(is(DEFAULT_LAST_NAME))
            .jsonPath("$.gender")
            .value(is(DEFAULT_GENDER.toString()))
            .jsonPath("$.email")
            .value(is(DEFAULT_EMAIL))
            .jsonPath("$.phone")
            .value(is(DEFAULT_PHONE))
            .jsonPath("$.phone2")
            .value(is(DEFAULT_PHONE_2))
            .jsonPath("$.addressLine1")
            .value(is(DEFAULT_ADDRESS_LINE_1))
            .jsonPath("$.addressLine2")
            .value(is(DEFAULT_ADDRESS_LINE_2))
            .jsonPath("$.city")
            .value(is(DEFAULT_CITY))
            .jsonPath("$.country")
            .value(is(DEFAULT_COUNTRY))
            .jsonPath("$.storeName")
            .value(is(DEFAULT_STORE_NAME))
            .jsonPath("$.businessAccountNumber")
            .value(is(DEFAULT_BUSINESS_ACCOUNT_NUMBER.intValue()));
    }

    @Test
    void getNonExistingVendor() {
        // Get the vendor
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewVendor() throws Exception {
        // Initialize the database
        vendorRepository.save(vendor).block();

        int databaseSizeBeforeUpdate = vendorRepository.findAll().collectList().block().size();

        // Update the vendor
        Vendor updatedVendor = vendorRepository.findById(vendor.getId()).block();
        updatedVendor
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .gender(UPDATED_GENDER)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE)
            .phone2(UPDATED_PHONE_2)
            .addressLine1(UPDATED_ADDRESS_LINE_1)
            .addressLine2(UPDATED_ADDRESS_LINE_2)
            .city(UPDATED_CITY)
            .country(UPDATED_COUNTRY)
            .storeName(UPDATED_STORE_NAME)
            .businessAccountNumber(UPDATED_BUSINESS_ACCOUNT_NUMBER);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedVendor.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedVendor))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Vendor in the database
        List<Vendor> vendorList = vendorRepository.findAll().collectList().block();
        assertThat(vendorList).hasSize(databaseSizeBeforeUpdate);
        Vendor testVendor = vendorList.get(vendorList.size() - 1);
        assertThat(testVendor.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testVendor.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testVendor.getGender()).isEqualTo(UPDATED_GENDER);
        assertThat(testVendor.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testVendor.getPhone()).isEqualTo(UPDATED_PHONE);
        assertThat(testVendor.getPhone2()).isEqualTo(UPDATED_PHONE_2);
        assertThat(testVendor.getAddressLine1()).isEqualTo(UPDATED_ADDRESS_LINE_1);
        assertThat(testVendor.getAddressLine2()).isEqualTo(UPDATED_ADDRESS_LINE_2);
        assertThat(testVendor.getCity()).isEqualTo(UPDATED_CITY);
        assertThat(testVendor.getCountry()).isEqualTo(UPDATED_COUNTRY);
        assertThat(testVendor.getStoreName()).isEqualTo(UPDATED_STORE_NAME);
        assertThat(testVendor.getBusinessAccountNumber()).isEqualTo(UPDATED_BUSINESS_ACCOUNT_NUMBER);
    }

    @Test
    void putNonExistingVendor() throws Exception {
        int databaseSizeBeforeUpdate = vendorRepository.findAll().collectList().block().size();
        vendor.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, vendor.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(vendor))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Vendor in the database
        List<Vendor> vendorList = vendorRepository.findAll().collectList().block();
        assertThat(vendorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchVendor() throws Exception {
        int databaseSizeBeforeUpdate = vendorRepository.findAll().collectList().block().size();
        vendor.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(vendor))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Vendor in the database
        List<Vendor> vendorList = vendorRepository.findAll().collectList().block();
        assertThat(vendorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamVendor() throws Exception {
        int databaseSizeBeforeUpdate = vendorRepository.findAll().collectList().block().size();
        vendor.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(vendor))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Vendor in the database
        List<Vendor> vendorList = vendorRepository.findAll().collectList().block();
        assertThat(vendorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateVendorWithPatch() throws Exception {
        // Initialize the database
        vendorRepository.save(vendor).block();

        int databaseSizeBeforeUpdate = vendorRepository.findAll().collectList().block().size();

        // Update the vendor using partial update
        Vendor partialUpdatedVendor = new Vendor();
        partialUpdatedVendor.setId(vendor.getId());

        partialUpdatedVendor
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .gender(UPDATED_GENDER)
            .phone(UPDATED_PHONE)
            .addressLine1(UPDATED_ADDRESS_LINE_1)
            .country(UPDATED_COUNTRY)
            .storeName(UPDATED_STORE_NAME);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedVendor.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedVendor))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Vendor in the database
        List<Vendor> vendorList = vendorRepository.findAll().collectList().block();
        assertThat(vendorList).hasSize(databaseSizeBeforeUpdate);
        Vendor testVendor = vendorList.get(vendorList.size() - 1);
        assertThat(testVendor.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testVendor.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testVendor.getGender()).isEqualTo(UPDATED_GENDER);
        assertThat(testVendor.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testVendor.getPhone()).isEqualTo(UPDATED_PHONE);
        assertThat(testVendor.getPhone2()).isEqualTo(DEFAULT_PHONE_2);
        assertThat(testVendor.getAddressLine1()).isEqualTo(UPDATED_ADDRESS_LINE_1);
        assertThat(testVendor.getAddressLine2()).isEqualTo(DEFAULT_ADDRESS_LINE_2);
        assertThat(testVendor.getCity()).isEqualTo(DEFAULT_CITY);
        assertThat(testVendor.getCountry()).isEqualTo(UPDATED_COUNTRY);
        assertThat(testVendor.getStoreName()).isEqualTo(UPDATED_STORE_NAME);
        assertThat(testVendor.getBusinessAccountNumber()).isEqualTo(DEFAULT_BUSINESS_ACCOUNT_NUMBER);
    }

    @Test
    void fullUpdateVendorWithPatch() throws Exception {
        // Initialize the database
        vendorRepository.save(vendor).block();

        int databaseSizeBeforeUpdate = vendorRepository.findAll().collectList().block().size();

        // Update the vendor using partial update
        Vendor partialUpdatedVendor = new Vendor();
        partialUpdatedVendor.setId(vendor.getId());

        partialUpdatedVendor
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .gender(UPDATED_GENDER)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE)
            .phone2(UPDATED_PHONE_2)
            .addressLine1(UPDATED_ADDRESS_LINE_1)
            .addressLine2(UPDATED_ADDRESS_LINE_2)
            .city(UPDATED_CITY)
            .country(UPDATED_COUNTRY)
            .storeName(UPDATED_STORE_NAME)
            .businessAccountNumber(UPDATED_BUSINESS_ACCOUNT_NUMBER);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedVendor.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedVendor))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Vendor in the database
        List<Vendor> vendorList = vendorRepository.findAll().collectList().block();
        assertThat(vendorList).hasSize(databaseSizeBeforeUpdate);
        Vendor testVendor = vendorList.get(vendorList.size() - 1);
        assertThat(testVendor.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testVendor.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testVendor.getGender()).isEqualTo(UPDATED_GENDER);
        assertThat(testVendor.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testVendor.getPhone()).isEqualTo(UPDATED_PHONE);
        assertThat(testVendor.getPhone2()).isEqualTo(UPDATED_PHONE_2);
        assertThat(testVendor.getAddressLine1()).isEqualTo(UPDATED_ADDRESS_LINE_1);
        assertThat(testVendor.getAddressLine2()).isEqualTo(UPDATED_ADDRESS_LINE_2);
        assertThat(testVendor.getCity()).isEqualTo(UPDATED_CITY);
        assertThat(testVendor.getCountry()).isEqualTo(UPDATED_COUNTRY);
        assertThat(testVendor.getStoreName()).isEqualTo(UPDATED_STORE_NAME);
        assertThat(testVendor.getBusinessAccountNumber()).isEqualTo(UPDATED_BUSINESS_ACCOUNT_NUMBER);
    }

    @Test
    void patchNonExistingVendor() throws Exception {
        int databaseSizeBeforeUpdate = vendorRepository.findAll().collectList().block().size();
        vendor.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, vendor.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(vendor))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Vendor in the database
        List<Vendor> vendorList = vendorRepository.findAll().collectList().block();
        assertThat(vendorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchVendor() throws Exception {
        int databaseSizeBeforeUpdate = vendorRepository.findAll().collectList().block().size();
        vendor.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(vendor))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Vendor in the database
        List<Vendor> vendorList = vendorRepository.findAll().collectList().block();
        assertThat(vendorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamVendor() throws Exception {
        int databaseSizeBeforeUpdate = vendorRepository.findAll().collectList().block().size();
        vendor.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(vendor))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Vendor in the database
        List<Vendor> vendorList = vendorRepository.findAll().collectList().block();
        assertThat(vendorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteVendor() {
        // Initialize the database
        vendorRepository.save(vendor).block();

        int databaseSizeBeforeDelete = vendorRepository.findAll().collectList().block().size();

        // Delete the vendor
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, vendor.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Vendor> vendorList = vendorRepository.findAll().collectList().block();
        assertThat(vendorList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
