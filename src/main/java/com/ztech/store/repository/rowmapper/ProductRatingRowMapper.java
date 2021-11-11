package com.ztech.store.repository.rowmapper;

import com.ztech.store.domain.ProductRating;
import com.ztech.store.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link ProductRating}, with proper type conversions.
 */
@Service
public class ProductRatingRowMapper implements BiFunction<Row, String, ProductRating> {

    private final ColumnConverter converter;

    public ProductRatingRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link ProductRating} stored in the database.
     */
    @Override
    public ProductRating apply(Row row, String prefix) {
        ProductRating entity = new ProductRating();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setRating(converter.fromRow(row, prefix + "_rating", Float.class));
        entity.setDate(converter.fromRow(row, prefix + "_date", Instant.class));
        entity.setUserId(converter.fromRow(row, prefix + "_user_id", Long.class));
        entity.setProductId(converter.fromRow(row, prefix + "_product_id", Long.class));
        return entity;
    }
}
