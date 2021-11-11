package com.ztech.store.web.rest;

import com.ztech.store.domain.Cart;
import com.ztech.store.repository.CartRepository;
import com.ztech.store.service.CartService;
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
 * REST controller for managing {@link com.ztech.store.domain.Cart}.
 */
@RestController
@RequestMapping("/api")
public class CartResource {

    private final Logger log = LoggerFactory.getLogger(CartResource.class);

    private static final String ENTITY_NAME = "cart";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CartService cartService;

    private final CartRepository cartRepository;

    public CartResource(CartService cartService, CartRepository cartRepository) {
        this.cartService = cartService;
        this.cartRepository = cartRepository;
    }

    /**
     * {@code POST  /carts} : Create a new cart.
     *
     * @param cart the cart to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new cart, or with status {@code 400 (Bad Request)} if the cart has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/carts")
    public Mono<ResponseEntity<Cart>> createCart(@Valid @RequestBody Cart cart) throws URISyntaxException {
        log.debug("REST request to save Cart : {}", cart);
        if (cart.getId() != null) {
            throw new BadRequestAlertException("A new cart cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return cartService
            .save(cart)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/carts/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /carts/:id} : Updates an existing cart.
     *
     * @param id the id of the cart to save.
     * @param cart the cart to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated cart,
     * or with status {@code 400 (Bad Request)} if the cart is not valid,
     * or with status {@code 500 (Internal Server Error)} if the cart couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/carts/{id}")
    public Mono<ResponseEntity<Cart>> updateCart(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Cart cart
    ) throws URISyntaxException {
        log.debug("REST request to update Cart : {}, {}", id, cart);
        if (cart.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, cart.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return cartRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return cartService
                    .save(cart)
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
     * {@code PATCH  /carts/:id} : Partial updates given fields of an existing cart, field will ignore if it is null
     *
     * @param id the id of the cart to save.
     * @param cart the cart to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated cart,
     * or with status {@code 400 (Bad Request)} if the cart is not valid,
     * or with status {@code 404 (Not Found)} if the cart is not found,
     * or with status {@code 500 (Internal Server Error)} if the cart couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/carts/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Cart>> partialUpdateCart(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Cart cart
    ) throws URISyntaxException {
        log.debug("REST request to partial update Cart partially : {}, {}", id, cart);
        if (cart.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, cart.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return cartRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Cart> result = cartService.partialUpdate(cart);

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
     * {@code GET  /carts} : get all the carts.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of carts in body.
     */
    @GetMapping("/carts")
    public Mono<List<Cart>> getAllCarts() {
        log.debug("REST request to get all Carts");
        return cartService.findAll().collectList();
    }

    /**
     * {@code GET  /carts} : get all the carts as a stream.
     * @return the {@link Flux} of carts.
     */
    @GetMapping(value = "/carts", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Cart> getAllCartsAsStream() {
        log.debug("REST request to get all Carts as a stream");
        return cartService.findAll();
    }

    /**
     * {@code GET  /carts/:id} : get the "id" cart.
     *
     * @param id the id of the cart to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the cart, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/carts/{id}")
    public Mono<ResponseEntity<Cart>> getCart(@PathVariable Long id) {
        log.debug("REST request to get Cart : {}", id);
        Mono<Cart> cart = cartService.findOne(id);
        return ResponseUtil.wrapOrNotFound(cart);
    }

    /**
     * {@code DELETE  /carts/:id} : delete the "id" cart.
     *
     * @param id the id of the cart to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/carts/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteCart(@PathVariable Long id) {
        log.debug("REST request to delete Cart : {}", id);
        return cartService
            .delete(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}
