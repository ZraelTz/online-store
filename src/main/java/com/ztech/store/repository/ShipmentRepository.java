package com.ztech.store.repository;

import com.ztech.store.domain.Shipment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Shipment entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ShipmentRepository extends R2dbcRepository<Shipment, Long>, ShipmentRepositoryInternal {
    Flux<Shipment> findAllBy(Pageable pageable);

    @Query("SELECT * FROM shipment entity WHERE entity.invoice_id = :id")
    Flux<Shipment> findByInvoice(Long id);

    @Query("SELECT * FROM shipment entity WHERE entity.invoice_id IS NULL")
    Flux<Shipment> findAllWhereInvoiceIsNull();

    @Query("select * from shipment sh cross join product_order po cross join customer c cross join jhi_user u where sh.invoice_id=po.id and po.customer_id=c.id and c.user_id=u.id and u.login=:login")
    Flux<Shipment> findAllByInvoiceOrderCustomerUserLogin(String currentUserLogin, Pageable pageable);

    @Query("select * from shipment sh cross join product_order po cross join customer c cross join jhi_user u where sh.invoice_id=po.id and po.customer_id=c.id and c.user_id=u.id and u.login=:login and sh.id=:id")
    Mono<Shipment> findOneByIdAndCustomerUserLogin(Long id, String currentUserLogin);

    // just to avoid having unambigous methods
    @Override
    Flux<Shipment> findAll();

    @Override
    Mono<Shipment> findById(Long id);

    @Override
    <S extends Shipment> Mono<S> save(S entity);
}

interface ShipmentRepositoryInternal {
    <S extends Shipment> Mono<S> insert(S entity);
    <S extends Shipment> Mono<S> save(S entity);
    Mono<Integer> update(Shipment entity);

    Flux<Shipment> findAll();
    Mono<Shipment> findById(Long id);
    Flux<Shipment> findAllBy(Pageable pageable);
    Flux<Shipment> findAllBy(Pageable pageable, Criteria criteria);
}
