package com.ztech.store.web.rest;

import com.ztech.store.domain.OrderedItem;
import com.ztech.store.repository.OrderedItemRepository;
import com.ztech.store.service.OrderedItemService;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.ztech.store.domain.OrderedItem}.
 */
@RestController
@RequestMapping("/api")
public class OrderedItemResource {

    private final Logger log = LoggerFactory.getLogger(OrderedItemResource.class);

    private static final String ENTITY_NAME = "orderedItem";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final OrderedItemService orderedItemService;

    private final OrderedItemRepository orderedItemRepository;

    public OrderedItemResource(OrderedItemService orderedItemService, OrderedItemRepository orderedItemRepository) {
        this.orderedItemService = orderedItemService;
        this.orderedItemRepository = orderedItemRepository;
    }

    /**
     * {@code POST  /ordered-items} : Create a new orderedItem.
     *
     * @param orderedItem the orderedItem to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new orderedItem, or with status {@code 400 (Bad Request)} if the orderedItem has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
 
    @PostMapping("/ordered-items")
    public Mono<ResponseEntity<OrderedItem>> createOrderedItem(@Valid @RequestBody OrderedItem orderedItem) throws URISyntaxException {
        log.debug("REST request to save OrderedItem : {}", orderedItem);
        if (orderedItem.getId() != null) {
            throw new BadRequestAlertException("A new orderedItem cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return orderedItemService
            .save(orderedItem)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/ordered-items/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /ordered-items/:id} : Updates an existing orderedItem.
     *
     * @param id the id of the orderedItem to save.
     * @param orderedItem the orderedItem to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated orderedItem,
     * or with status {@code 400 (Bad Request)} if the orderedItem is not valid,
     * or with status {@code 500 (Internal Server Error)} if the orderedItem couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/ordered-items/{id}")
    public Mono<ResponseEntity<OrderedItem>> updateOrderedItem(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody OrderedItem orderedItem
    ) throws URISyntaxException {
        log.debug("REST request to update OrderedItem : {}, {}", id, orderedItem);
        if (orderedItem.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, orderedItem.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return orderedItemRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return orderedItemService
                    .save(orderedItem)
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
     * {@code PATCH  /ordered-items/:id} : Partial updates given fields of an existing orderedItem, field will ignore if it is null
     *
     * @param id the id of the orderedItem to save.
     * @param orderedItem the orderedItem to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated orderedItem,
     * or with status {@code 400 (Bad Request)} if the orderedItem is not valid,
     * or with status {@code 404 (Not Found)} if the orderedItem is not found,
     * or with status {@code 500 (Internal Server Error)} if the orderedItem couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/ordered-items/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<OrderedItem>> partialUpdateOrderedItem(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody OrderedItem orderedItem
    ) throws URISyntaxException {
        log.debug("REST request to partial update OrderedItem partially : {}, {}", id, orderedItem);
        if (orderedItem.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, orderedItem.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return orderedItemRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<OrderedItem> result = orderedItemService.partialUpdate(orderedItem);

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
     * {@code GET  /ordered-items} : get all the orderedItems.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of orderedItems in body.
     */

    @GetMapping("/ordered-items")
    public Mono<ResponseEntity<List<OrderedItem>>> getAllOrderedItems(Pageable pageable, ServerHttpRequest request) {
        log.debug("REST request to get a page of OrderedItems");
        return orderedItemService
            .countAll()
            .zipWith(orderedItemService.findAll(pageable).collectList())
            .map(countWithEntities -> {
                return ResponseEntity
                    .ok()
                    .headers(
                        PaginationUtil.generatePaginationHttpHeaders(
                            UriComponentsBuilder.fromHttpRequest(request),
                            new PageImpl<>(countWithEntities.getT2(), pageable, countWithEntities.getT1())
                        )
                    )
                    .body(countWithEntities.getT2());
            });
    }

    /**
     * {@code GET  /ordered-items/:id} : get the "id" orderedItem.
     *
     * @param id the id of the orderedItem to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the orderedItem, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/ordered-items/{id}")
    public Mono<ResponseEntity<OrderedItem>> getOrderedItem(@PathVariable Long id) {
        log.debug("REST request to get OrderedItem : {}", id);
        Mono<OrderedItem> orderedItem = orderedItemService.findOne(id);
        return ResponseUtil.wrapOrNotFound(orderedItem);
    }

    /**
     * {@code DELETE  /ordered-items/:id} : delete the "id" orderedItem.
     *
     * @param id the id of the orderedItem to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/ordered-items/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteOrderedItem(@PathVariable Long id) {
        log.debug("REST request to delete OrderedItem : {}", id);
        return orderedItemService
            .delete(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}
