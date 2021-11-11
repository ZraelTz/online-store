package com.ztech.store.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import com.ztech.store.domain.CartItem;
import com.ztech.store.repository.rowmapper.CartItemRowMapper;
import com.ztech.store.repository.rowmapper.CartRowMapper;
import com.ztech.store.repository.rowmapper.CustomerRowMapper;
import com.ztech.store.repository.rowmapper.ProductRowMapper;
import com.ztech.store.service.EntityManager;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiFunction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive custom repository implementation for the CartItem entity.
 */
@SuppressWarnings("unused")
class CartItemRepositoryInternalImpl implements CartItemRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final ProductRowMapper productMapper;
    private final CustomerRowMapper customerMapper;
    private final CartRowMapper cartMapper;
    private final CartItemRowMapper cartitemMapper;

    private static final Table entityTable = Table.aliased("cart_item", EntityManager.ENTITY_ALIAS);
    private static final Table productTable = Table.aliased("product", "product");
    private static final Table customerTable = Table.aliased("customer", "customer");
    private static final Table cartTable = Table.aliased("cart", "cart");

    public CartItemRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        ProductRowMapper productMapper,
        CustomerRowMapper customerMapper,
        CartRowMapper cartMapper,
        CartItemRowMapper cartitemMapper
    ) {
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.productMapper = productMapper;
        this.customerMapper = customerMapper;
        this.cartMapper = cartMapper;
        this.cartitemMapper = cartitemMapper;
    }

    @Override
    public Flux<CartItem> findAllBy(Pageable pageable) {
        return findAllBy(pageable, null);
    }

    @Override
    public Flux<CartItem> findAllBy(Pageable pageable, Criteria criteria) {
        return createQuery(pageable, criteria).all();
    }

    RowsFetchSpec<CartItem> createQuery(Pageable pageable, Criteria criteria) {
        List<Expression> columns = CartItemSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(ProductSqlHelper.getColumns(productTable, "product"));
        columns.addAll(CustomerSqlHelper.getColumns(customerTable, "customer"));
        columns.addAll(CartSqlHelper.getColumns(cartTable, "cart"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(productTable)
            .on(Column.create("product_id", entityTable))
            .equals(Column.create("id", productTable))
            .leftOuterJoin(customerTable)
            .on(Column.create("customer_id", entityTable))
            .equals(Column.create("id", customerTable))
            .leftOuterJoin(cartTable)
            .on(Column.create("cart_id", entityTable))
            .equals(Column.create("id", cartTable));

        String select = entityManager.createSelect(selectFrom, CartItem.class, pageable, criteria);
        String alias = entityTable.getReferenceName().getReference();
        String selectWhere = Optional
            .ofNullable(criteria)
            .map(crit ->
                new StringBuilder(select)
                    .append(" ")
                    .append("WHERE")
                    .append(" ")
                    .append(alias)
                    .append(".")
                    .append(crit.toString())
                    .toString()
            )
            .orElse(select); // TODO remove once https://github.com/spring-projects/spring-data-jdbc/issues/907 will be fixed
        return db.sql(selectWhere).map(this::process);
    }

    @Override
    public Flux<CartItem> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<CartItem> findById(Long id) {
        return createQuery(null, where("id").is(id)).one();
    }

    private CartItem process(Row row, RowMetadata metadata) {
        CartItem entity = cartitemMapper.apply(row, "e");
        entity.setProduct(productMapper.apply(row, "product"));
        entity.setCustomer(customerMapper.apply(row, "customer"));
        entity.setCart(cartMapper.apply(row, "cart"));
        return entity;
    }

    @Override
    public <S extends CartItem> Mono<S> insert(S entity) {
        return entityManager.insert(entity);
    }

    @Override
    public <S extends CartItem> Mono<S> save(S entity) {
        if (entity.getId() == null) {
            return insert(entity);
        } else {
            return update(entity)
                .map(numberOfUpdates -> {
                    if (numberOfUpdates.intValue() <= 0) {
                        throw new IllegalStateException("Unable to update CartItem with id = " + entity.getId());
                    }
                    return entity;
                });
        }
    }

    @Override
    public Mono<Integer> update(CartItem entity) {
        //fixme is this the proper way?
        return r2dbcEntityTemplate.update(entity).thenReturn(1);
    }
}

class CartItemSqlHelper {

    static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("quantity", table, columnPrefix + "_quantity"));

        columns.add(Column.aliased("product_id", table, columnPrefix + "_product_id"));
        columns.add(Column.aliased("customer_id", table, columnPrefix + "_customer_id"));
        columns.add(Column.aliased("cart_id", table, columnPrefix + "_cart_id"));
        return columns;
    }
}
