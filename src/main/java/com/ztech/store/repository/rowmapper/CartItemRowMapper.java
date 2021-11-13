package com.ztech.store.repository.rowmapper;

import com.ztech.store.domain.CartItem;
import com.ztech.store.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link CartItem}, with proper type conversions.
 */
@Service
public class CartItemRowMapper implements BiFunction<Row, String, CartItem> {

    private final ColumnConverter converter;

    public CartItemRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link CartItem} stored in the database.
     */
    @Override
    public CartItem apply(Row row, String prefix) {
        CartItem entity = new CartItem();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setQuantity(converter.fromRow(row, prefix + "_quantity", Integer.class));
        entity.setCartId(converter.fromRow(row, prefix + "_cart_id", Long.class));
        entity.setProductId(converter.fromRow(row, prefix + "_product_id", Long.class));
        return entity;
    }
}
