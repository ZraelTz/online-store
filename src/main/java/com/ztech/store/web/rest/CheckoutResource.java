package com.ztech.store.web.rest;

import com.google.gson.Gson;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import com.ztech.store.domain.Checkout;
import com.ztech.store.domain.CreatePayment;
import com.ztech.store.domain.CreatePaymentResponse;
import com.ztech.store.repository.CheckoutRepository;
import com.ztech.store.service.CheckoutService;
import com.ztech.store.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.ztech.store.domain.Checkout}.
 */
@RestController
@RequestMapping("/api")
public class CheckoutResource {

    private final Logger log = LoggerFactory.getLogger(CheckoutResource.class);

    private static final String ENTITY_NAME = "checkout";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CheckoutService checkoutService;

    private final CheckoutRepository checkoutRepository;

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    public CheckoutResource(CheckoutService checkoutService, CheckoutRepository checkoutRepository) {
        this.checkoutService = checkoutService;
        this.checkoutRepository = checkoutRepository;
        Stripe.apiKey = stripeApiKey;
    }

    /**
     * {@code POST  /checkouts} : Create a new checkout.
     *
     * @param checkout the checkout to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new checkout, or with status {@code 400 (Bad Request)} if the checkout has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/checkouts")
    public Mono<ResponseEntity<Checkout>> createCheckout(@Valid @RequestBody Checkout checkout) throws URISyntaxException {
        log.debug("REST request to save Checkout : {}", checkout);
        if (checkout.getId() != null) {
            throw new BadRequestAlertException("A new checkout cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return checkoutService
            .save(checkout)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/checkouts/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /checkouts/:id} : Updates an existing checkout.
     *
     * @param id the id of the checkout to save.
     * @param checkout the checkout to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated checkout,
     * or with status {@code 400 (Bad Request)} if the checkout is not valid,
     * or with status {@code 500 (Internal Server Error)} if the checkout couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/checkouts/{id}")
    public Mono<ResponseEntity<Checkout>> updateCheckout(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Checkout checkout
    ) throws URISyntaxException {
        log.debug("REST request to update Checkout : {}, {}", id, checkout);
        if (checkout.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, checkout.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return checkoutRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return checkoutService
                    .save(checkout)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /checkouts/:id} : Partial updates given fields of an existing checkout, field will ignore if it is null
     *
     * @param id the id of the checkout to save.
     * @param checkout the checkout to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated checkout,
     * or with status {@code 400 (Bad Request)} if the checkout is not valid,
     * or with status {@code 404 (Not Found)} if the checkout is not found,
     * or with status {@code 500 (Internal Server Error)} if the checkout couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/checkouts/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Checkout>> partialUpdateCheckout(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Checkout checkout
    ) throws URISyntaxException {
        log.debug("REST request to partial update Checkout partially : {}, {}", id, checkout);
        if (checkout.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, checkout.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return checkoutRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Checkout> result = checkoutService.partialUpdate(checkout);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /checkouts} : get all the checkouts.
     *
     * @param filter the filter of the request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of checkouts in body.
     */
    @GetMapping("/checkouts")
    public Mono<List<Checkout>> getAllCheckouts(@RequestParam(required = false) String filter) {
        if ("cart-is-null".equals(filter)) {
            log.debug("REST request to get all Checkouts where cart is null");
            return checkoutService.findAllWhereCartIsNull().collectList();
        }
        log.debug("REST request to get all Checkouts");
        return checkoutService.findAll().collectList();
    }

    /**
     * {@code GET  /checkouts} : get all the checkouts as a stream.
     * @return the {@link Flux} of checkouts.
     */
    @GetMapping(value = "/checkouts", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Checkout> getAllCheckoutsAsStream() {
        log.debug("REST request to get all Checkouts as a stream");
        return checkoutService.findAll();
    }

    /**
     * {@code GET  /checkouts/:id} : get the "id" checkout.
     *
     * @param id the id of the checkout to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the checkout, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/checkouts/{id}")
    public Mono<ResponseEntity<Checkout>> getCheckout(@PathVariable Long id) {
        log.debug("REST request to get Checkout : {}", id);
        Mono<Checkout> checkout = checkoutService.findOne(id);
        return ResponseUtil.wrapOrNotFound(checkout);
    }

    /**
     * {@code DELETE  /checkouts/:id} : delete the "id" checkout.
     *
     * @param id the id of the checkout to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/checkouts/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteCheckout(@PathVariable Long id) {
        log.debug("REST request to delete Checkout : {}", id);
        return checkoutService
            .delete(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                    .build()
            );
    }

    @PostMapping("/create-payment-intent")
    public Mono<CreatePaymentResponse> createPaymentIntent(@Valid @RequestBody Checkout checkout) throws StripeException{

            List<String> paymentMethodTypes = new ArrayList<>();
            paymentMethodTypes.add("sepa_debit");
            paymentMethodTypes.add("card");
            PaymentIntentCreateParams params =
              PaymentIntentCreateParams.builder()
                .setAmount(checkout.getTotalPrice().longValue() * 100L)
                .setCurrency("usd")
                .addAllPaymentMethodType(paymentMethodTypes)
                .build();
      
            // Create a PaymentIntent with the order amount and currency
            PaymentIntent paymentIntent = PaymentIntent.create(params);
      
            return Mono.just(new CreatePaymentResponse(paymentIntent.getClientSecret()));
            
    }
}
