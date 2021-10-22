package com.ztech.store.repository.rowmapper;

import com.ztech.store.domain.OrderedItem;
import com.ztech.store.domain.enumeration.OrderedItemStatus;
import com.ztech.store.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.math.BigDecimal;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link OrderedItem}, with proper type conversions.
 */
@Service
public class OrderedItemRowMapper implements BiFunction<Row, String, OrderedItem> {

    private final ColumnConverter converter;

    public OrderedItemRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link OrderedItem} stored in the database.
     */
    @Override
    public OrderedItem apply(Row row, String prefix) {
        OrderedItem entity = new OrderedItem();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setQuantity(converter.fromRow(row, prefix + "_quantity", Integer.class));
        entity.setTotalPrice(converter.fromRow(row, prefix + "_total_price", BigDecimal.class));
        entity.setStatus(converter.fromRow(row, prefix + "_status", OrderedItemStatus.class));
        entity.setProductId(converter.fromRow(row, prefix + "_product_id", Long.class));
        entity.setProductOrderId(converter.fromRow(row, prefix + "_product_order_id", Long.class));
        return entity;
    }
}
