package com.ztech.store.repository;

import com.ztech.store.domain.Vendor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Vendor entity.
 */
@SuppressWarnings("unused")
@Repository
public interface VendorRepository extends R2dbcRepository<Vendor, Long>, VendorRepositoryInternal {
    @Query("SELECT * FROM vendor entity WHERE entity.user_id = :id")
    Flux<Vendor> findByUser(Long id);

    @Query("SELECT * FROM vendor entity WHERE entity.user_id IS NULL")
    Flux<Vendor> findAllWhereUserIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<Vendor> findAll();

    @Override
    Mono<Vendor> findById(Long id);

    @Override
    <S extends Vendor> Mono<S> save(S entity);
}

interface VendorRepositoryInternal {
    <S extends Vendor> Mono<S> insert(S entity);
    <S extends Vendor> Mono<S> save(S entity);
    Mono<Integer> update(Vendor entity);

    Flux<Vendor> findAll();
    Mono<Vendor> findById(Long id);
    Flux<Vendor> findAllBy(Pageable pageable);
    Flux<Vendor> findAllBy(Pageable pageable, Criteria criteria);
}
