package com.ztech.store.repository;

import com.ztech.store.domain.Checkout;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Checkout entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CheckoutRepository extends R2dbcRepository<Checkout, Long>, CheckoutRepositoryInternal {
    @Query("SELECT * FROM checkout entity WHERE entity.id not in (select checkout_id from cart)")
    Flux<Checkout> findAllWhereCartIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<Checkout> findAll();

    @Override
    Mono<Checkout> findById(Long id);

    @Override
    <S extends Checkout> Mono<S> save(S entity);
}

interface CheckoutRepositoryInternal {
    <S extends Checkout> Mono<S> insert(S entity);
    <S extends Checkout> Mono<S> save(S entity);
    Mono<Integer> update(Checkout entity);

    Flux<Checkout> findAll();
    Mono<Checkout> findById(Long id);
    Flux<Checkout> findAllBy(Pageable pageable);
    Flux<Checkout> findAllBy(Pageable pageable, Criteria criteria);
}
