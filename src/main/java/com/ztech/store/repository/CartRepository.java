package com.ztech.store.repository;

import com.ztech.store.domain.Cart;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Cart entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CartRepository extends R2dbcRepository<Cart, Long>, CartRepositoryInternal {
    @Query("SELECT * FROM cart entity WHERE entity.user_id = :id")
    Flux<Cart> findByUser(Long id);

    @Query("SELECT * FROM cart entity WHERE entity.user_id IS NULL")
    Flux<Cart> findAllWhereUserIsNull();

    @Query("SELECT * FROM cart entity WHERE entity.checkout_id = :id")
    Flux<Cart> findByCheckout(Long id);

    @Query("SELECT * FROM cart entity WHERE entity.checkout_id IS NULL")
    Flux<Cart> findAllWhereCheckoutIsNull();

    @Query("select * from cart ct cross join customer c cross join jhi_user u where ct.customer_id=c.id and c.user_id=u.id and u.login=:login")
    Flux<Cart> findAllByCustomerUserLogin(String userLogin);

    // just to avoid having unambigous methods
    @Override
    Flux<Cart> findAll();

    @Override
    Mono<Cart> findById(Long id);

    @Override
    <S extends Cart> Mono<S> save(S entity);
}

interface CartRepositoryInternal {
    <S extends Cart> Mono<S> insert(S entity);
    <S extends Cart> Mono<S> save(S entity);
    Mono<Integer> update(Cart entity);

    Flux<Cart> findAll();
    Mono<Cart> findById(Long id);
    Flux<Cart> findAllBy(Pageable pageable);
    Flux<Cart> findAllBy(Pageable pageable, Criteria criteria);
}
