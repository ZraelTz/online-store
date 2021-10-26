package com.ztech.store.web.rest;

import com.ztech.store.domain.ProductRating;
import com.ztech.store.repository.ProductRatingRepository;
import com.ztech.store.service.ProductRatingService;
import com.ztech.store.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
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
 * REST controller for managing {@link com.ztech.store.domain.ProductRating}.
 */
@RestController
@RequestMapping("/api")
public class ProductRatingResource {

    private final Logger log = LoggerFactory.getLogger(ProductRatingResource.class);

    private static final String ENTITY_NAME = "productRating";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProductRatingService productRatingService;

    private final ProductRatingRepository productRatingRepository;

    public ProductRatingResource(ProductRatingService productRatingService, ProductRatingRepository productRatingRepository) {
        this.productRatingService = productRatingService;
        this.productRatingRepository = productRatingRepository;
    }

    /**
     * {@code POST  /product-ratings} : Create a new productRating.
     *
     * @param productRating the productRating to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new productRating, or with status {@code 400 (Bad Request)} if the productRating has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/product-ratings")
    public Mono<ResponseEntity<ProductRating>> createProductRating(@Valid @RequestBody ProductRating productRating)
        throws URISyntaxException {
        log.debug("REST request to save ProductRating : {}", productRating);
        if (productRating.getId() != null) {
            throw new BadRequestAlertException("A new productRating cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return productRatingService
            .save(productRating)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/product-ratings/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /product-ratings/:id} : Updates an existing productRating.
     *
     * @param id the id of the productRating to save.
     * @param productRating the productRating to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated productRating,
     * or with status {@code 400 (Bad Request)} if the productRating is not valid,
     * or with status {@code 500 (Internal Server Error)} if the productRating couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/product-ratings/{id}")
    public Mono<ResponseEntity<ProductRating>> updateProductRating(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ProductRating productRating
    ) throws URISyntaxException {
        log.debug("REST request to update ProductRating : {}, {}", id, productRating);
        if (productRating.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, productRating.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return productRatingRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return productRatingService
                    .save(productRating)
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
     * {@code PATCH  /product-ratings/:id} : Partial updates given fields of an existing productRating, field will ignore if it is null
     *
     * @param id the id of the productRating to save.
     * @param productRating the productRating to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated productRating,
     * or with status {@code 400 (Bad Request)} if the productRating is not valid,
     * or with status {@code 404 (Not Found)} if the productRating is not found,
     * or with status {@code 500 (Internal Server Error)} if the productRating couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/product-ratings/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<ProductRating>> partialUpdateProductRating(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ProductRating productRating
    ) throws URISyntaxException {
        log.debug("REST request to partial update ProductRating partially : {}, {}", id, productRating);
        if (productRating.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, productRating.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return productRatingRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<ProductRating> result = productRatingService.partialUpdate(productRating);

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
     * {@code GET  /product-ratings} : get all the productRatings.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of productRatings in body.
     */
    @GetMapping("/product-ratings")
    public Mono<List<ProductRating>> getAllProductRatings() {
        log.debug("REST request to get all ProductRatings");
        return productRatingService.findAll().collectList();
    }

    /**
     * {@code GET  /product-ratings} : get all the productRatings as a stream.
     * @return the {@link Flux} of productRatings.
     */
    @GetMapping(value = "/product-ratings", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<ProductRating> getAllProductRatingsAsStream() {
        log.debug("REST request to get all ProductRatings as a stream");
        return productRatingService.findAll();
    }

    /**
     * {@code GET  /product-ratings/:id} : get the "id" productRating.
     *
     * @param id the id of the productRating to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the productRating, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/product-ratings/{id}")
    public Mono<ResponseEntity<ProductRating>> getProductRating(@PathVariable Long id) {
        log.debug("REST request to get ProductRating : {}", id);
        Mono<ProductRating> productRating = productRatingService.findOne(id);
        return ResponseUtil.wrapOrNotFound(productRating);
    }

    /**
     * {@code DELETE  /product-ratings/:id} : delete the "id" productRating.
     *
     * @param id the id of the productRating to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/product-ratings/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteProductRating(@PathVariable Long id) {
        log.debug("REST request to delete ProductRating : {}", id);
        return productRatingService
            .delete(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}
