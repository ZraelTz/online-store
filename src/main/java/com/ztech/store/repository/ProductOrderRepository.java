package com.ztech.store.repository;

import com.ztech.store.domain.ProductOrder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the ProductOrder entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProductOrderRepository extends R2dbcRepository<ProductOrder, Long>, ProductOrderRepositoryInternal {
    Flux<ProductOrder> findAllBy(Pageable pageable);

    @Query("SELECT * FROM product_order entity WHERE entity.customer_id = :id")
    Flux<ProductOrder> findByCustomer(Long id);

    @Query("SELECT * FROM product_order entity WHERE entity.customer_id IS NULL")
    Flux<ProductOrder> findAllWhereCustomerIsNull();

    @Query("select * from product_order po cross join customer c cross join jhi_user u where po.customer_id=c.id and c.user_id=u.id and u.login=:login")
    Flux<ProductOrder> findAllByCustomerUserLogin(String currentUserLogin,
    Pageable pageable);

    // just to avoid having unambigous methods
    @Override
    Flux<ProductOrder> findAll();

    @Override
    Mono<ProductOrder> findById(Long id);

    @Override
    <S extends ProductOrder> Mono<S> save(S entity);

}

interface ProductOrderRepositoryInternal {
    <S extends ProductOrder> Mono<S> insert(S entity);
    <S extends ProductOrder> Mono<S> save(S entity);
    Mono<Integer> update(ProductOrder entity);

    Flux<ProductOrder> findAll();
    Mono<ProductOrder> findById(Long id);
    Flux<ProductOrder> findAllBy(Pageable pageable);
    Flux<ProductOrder> findAllBy(Pageable pageable, Criteria criteria);
}
