package com.ztech.store.repository.rowmapper;

import com.ztech.store.domain.ProductRating;
import com.ztech.store.service.ColumnConverter;
import io.r2dbc.spi.Row;
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
        entity.setValue(converter.fromRow(row, prefix + "_value", Float.class));
        entity.setProductId(converter.fromRow(row, prefix + "_product_id", Long.class));
        entity.setUserId(converter.fromRow(row, prefix + "_user_id", Long.class));
        entity.setProductRatingId(converter.fromRow(row, prefix + "_product_rating_id", Long.class));
        entity.setRatingId(converter.fromRow(row, prefix + "_rating_id", Long.class));
        return entity;
    }
}
