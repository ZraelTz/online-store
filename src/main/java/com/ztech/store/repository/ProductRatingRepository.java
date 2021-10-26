package com.ztech.store.repository;

import com.ztech.store.domain.ProductRating;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the ProductRating entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProductRatingRepository extends R2dbcRepository<ProductRating, Long>, ProductRatingRepositoryInternal {
    @Query("SELECT * FROM product_rating entity WHERE entity.product_rating_id = :id")
    Flux<ProductRating> findByProductRating(Long id);

    @Query("SELECT * FROM product_rating entity WHERE entity.product_rating_id IS NULL")
    Flux<ProductRating> findAllWhereProductRatingIsNull();

    @Query("SELECT * FROM product_rating entity WHERE entity.rating_id = :id")
    Flux<ProductRating> findByRating(Long id);

    @Query("SELECT * FROM product_rating entity WHERE entity.rating_id IS NULL")
    Flux<ProductRating> findAllWhereRatingIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<ProductRating> findAll();

    @Override
    Mono<ProductRating> findById(Long id);

    @Override
    <S extends ProductRating> Mono<S> save(S entity);
}

interface ProductRatingRepositoryInternal {
    <S extends ProductRating> Mono<S> insert(S entity);
    <S extends ProductRating> Mono<S> save(S entity);
    Mono<Integer> update(ProductRating entity);

    Flux<ProductRating> findAll();
    Mono<ProductRating> findById(Long id);
    Flux<ProductRating> findAllBy(Pageable pageable);
    Flux<ProductRating> findAllBy(Pageable pageable, Criteria criteria);
}
