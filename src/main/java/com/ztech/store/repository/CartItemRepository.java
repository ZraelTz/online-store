package com.ztech.store.repository;

import com.ztech.store.domain.CartItem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the CartItem entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CartItemRepository extends R2dbcRepository<CartItem, Long>, CartItemRepositoryInternal {
    Flux<CartItem> findAllBy(Pageable pageable);

    @Query("SELECT * FROM cart_item entity WHERE entity.cart_id = :id")
    Flux<CartItem> findByCart(Long id);

    @Query("SELECT * FROM cart_item entity WHERE entity.cart_id IS NULL")
    Flux<CartItem> findAllWhereCartIsNull();

    @Query("SELECT * FROM cart_item entity WHERE entity.product_id = :id")
    Flux<CartItem> findByProduct(Long id);

    @Query("SELECT * FROM cart_item entity WHERE entity.product_id IS NULL")
    Flux<CartItem> findAllWhereProductIsNull();

    @Query("select * from cart_item ci cross join cart ct cross join customer c cross join jhi_user u where ci.id=ct.id and ct.customer_id=c.id and c.user_id=u.id and u.login=:login")
    Flux<CartItem> findAllByCustomerUserLogin(String currentUserLogin, Pageable pageable);

    @Query("select * from cart_item ci cross join cart ct cross join customer c cross join jhi_user u where ci.id=ct.id and ct.customer_id=c.id and c.user_id=u.id and u.login=:login and ci.id=:id")
    Mono<CartItem> findOneByIdAndCustomerUserLogin(Long id, String currentUserLogin);

    // just to avoid having unambigous methods
    @Override
    Flux<CartItem> findAll();

    @Override
    Mono<CartItem> findById(Long id);

    @Override
    <S extends CartItem> Mono<S> save(S entity);
}

interface CartItemRepositoryInternal {
    <S extends CartItem> Mono<S> insert(S entity);
    <S extends CartItem> Mono<S> save(S entity);
    Mono<Integer> update(CartItem entity);

    Flux<CartItem> findAll();
    Mono<CartItem> findById(Long id);
    Flux<CartItem> findAllBy(Pageable pageable);
    Flux<CartItem> findAllBy(Pageable pageable, Criteria criteria);
}
