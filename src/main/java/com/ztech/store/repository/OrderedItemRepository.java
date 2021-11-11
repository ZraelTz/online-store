package com.ztech.store.repository;

import com.ztech.store.domain.OrderedItem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the OrderedItem entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OrderedItemRepository extends R2dbcRepository<OrderedItem, Long>, OrderedItemRepositoryInternal {
    Flux<OrderedItem> findAllBy(Pageable pageable);

    @Query("SELECT * FROM ordered_item entity WHERE entity.product_id = :id")
    Flux<OrderedItem> findByProduct(Long id);

    @Query("SELECT * FROM ordered_item entity WHERE entity.product_id IS NULL")
    Flux<OrderedItem> findAllWhereProductIsNull();

    @Query("SELECT * FROM ordered_item entity WHERE entity.product_order_id = :id")
    Flux<OrderedItem> findByProductOrder(Long id);

    @Query("SELECT * FROM ordered_item entity WHERE entity.product_order_id IS NULL")
    Flux<OrderedItem> findAllWhereProductOrderIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<OrderedItem> findAll();

    @Override
    Mono<OrderedItem> findById(Long id);

    @Override
    <S extends OrderedItem> Mono<S> save(S entity);
}

interface OrderedItemRepositoryInternal {
    <S extends OrderedItem> Mono<S> insert(S entity);
    <S extends OrderedItem> Mono<S> save(S entity);
    Mono<Integer> update(OrderedItem entity);

    Flux<OrderedItem> findAll();
    Mono<OrderedItem> findById(Long id);
    Flux<OrderedItem> findAllBy(Pageable pageable);
    Flux<OrderedItem> findAllBy(Pageable pageable, Criteria criteria);
}
