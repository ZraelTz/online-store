package com.ztech.store.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import com.ztech.store.domain.ProductRating;
import com.ztech.store.repository.rowmapper.ProductRatingRowMapper;
import com.ztech.store.repository.rowmapper.ProductRowMapper;
import com.ztech.store.repository.rowmapper.UserRowMapper;
import com.ztech.store.service.EntityManager;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.time.Instant;
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
 * Spring Data SQL reactive custom repository implementation for the ProductRating entity.
 */
@SuppressWarnings("unused")
class ProductRatingRepositoryInternalImpl implements ProductRatingRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final UserRowMapper userMapper;
    private final ProductRowMapper productMapper;
    private final ProductRatingRowMapper productratingMapper;

    private static final Table entityTable = Table.aliased("product_rating", EntityManager.ENTITY_ALIAS);
    private static final Table userTable = Table.aliased("jhi_user", "e_user");
    private static final Table productTable = Table.aliased("product", "product");

    public ProductRatingRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        UserRowMapper userMapper,
        ProductRowMapper productMapper,
        ProductRatingRowMapper productratingMapper
    ) {
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.userMapper = userMapper;
        this.productMapper = productMapper;
        this.productratingMapper = productratingMapper;
    }

    @Override
    public Flux<ProductRating> findAllBy(Pageable pageable) {
        return findAllBy(pageable, null);
    }

    @Override
    public Flux<ProductRating> findAllBy(Pageable pageable, Criteria criteria) {
        return createQuery(pageable, criteria).all();
    }

    RowsFetchSpec<ProductRating> createQuery(Pageable pageable, Criteria criteria) {
        List<Expression> columns = ProductRatingSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(UserSqlHelper.getColumns(userTable, "user"));
        columns.addAll(ProductSqlHelper.getColumns(productTable, "product"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(userTable)
            .on(Column.create("user_id", entityTable))
            .equals(Column.create("id", userTable))
            .leftOuterJoin(productTable)
            .on(Column.create("product_id", entityTable))
            .equals(Column.create("id", productTable));

        String select = entityManager.createSelect(selectFrom, ProductRating.class, pageable, criteria);
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
    public Flux<ProductRating> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<ProductRating> findById(Long id) {
        return createQuery(null, where("id").is(id)).one();
    }

    private ProductRating process(Row row, RowMetadata metadata) {
        ProductRating entity = productratingMapper.apply(row, "e");
        entity.setUser(userMapper.apply(row, "user"));
        entity.setProduct(productMapper.apply(row, "product"));
        return entity;
    }

    @Override
    public <S extends ProductRating> Mono<S> insert(S entity) {
        return entityManager.insert(entity);
    }

    @Override
    public <S extends ProductRating> Mono<S> save(S entity) {
        if (entity.getId() == null) {
            return insert(entity);
        } else {
            return update(entity)
                .map(numberOfUpdates -> {
                    if (numberOfUpdates.intValue() <= 0) {
                        throw new IllegalStateException("Unable to update ProductRating with id = " + entity.getId());
                    }
                    return entity;
                });
        }
    }

    @Override
    public Mono<Integer> update(ProductRating entity) {
        //fixme is this the proper way?
        return r2dbcEntityTemplate.update(entity).thenReturn(1);
    }
}

class ProductRatingSqlHelper {

    static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("rating", table, columnPrefix + "_rating"));
        columns.add(Column.aliased("date", table, columnPrefix + "_date"));

        columns.add(Column.aliased("user_id", table, columnPrefix + "_user_id"));
        columns.add(Column.aliased("product_id", table, columnPrefix + "_product_id"));
        return columns;
    }
}
