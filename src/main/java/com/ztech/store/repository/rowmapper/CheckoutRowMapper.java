package com.ztech.store.repository.rowmapper;

import com.ztech.store.domain.Checkout;
import com.ztech.store.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.math.BigDecimal;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Checkout}, with proper type conversions.
 */
@Service
public class CheckoutRowMapper implements BiFunction<Row, String, Checkout> {

    private final ColumnConverter converter;

    public CheckoutRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Checkout} stored in the database.
     */
    @Override
    public Checkout apply(Row row, String prefix) {
        Checkout entity = new Checkout();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setQuantity(converter.fromRow(row, prefix + "_quantity", Integer.class));
        entity.setTotalPrice(converter.fromRow(row, prefix + "_total_price", BigDecimal.class));
        return entity;
    }
}
