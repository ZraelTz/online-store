package com.ztech.store.repository.rowmapper;

import com.ztech.store.domain.Vendor;
import com.ztech.store.domain.enumeration.Gender;
import com.ztech.store.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Vendor}, with proper type conversions.
 */
@Service
public class VendorRowMapper implements BiFunction<Row, String, Vendor> {

    private final ColumnConverter converter;

    public VendorRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Vendor} stored in the database.
     */
    @Override
    public Vendor apply(Row row, String prefix) {
        Vendor entity = new Vendor();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setFirstName(converter.fromRow(row, prefix + "_first_name", String.class));
        entity.setLastName(converter.fromRow(row, prefix + "_last_name", String.class));
        entity.setGender(converter.fromRow(row, prefix + "_gender", Gender.class));
        entity.setEmail(converter.fromRow(row, prefix + "_email", String.class));
        entity.setPhone(converter.fromRow(row, prefix + "_phone", String.class));
        entity.setPhone2(converter.fromRow(row, prefix + "_phone_2", String.class));
        entity.setAddressLine1(converter.fromRow(row, prefix + "_address_line_1", String.class));
        entity.setAddressLine2(converter.fromRow(row, prefix + "_address_line_2", String.class));
        entity.setCity(converter.fromRow(row, prefix + "_city", String.class));
        entity.setCountry(converter.fromRow(row, prefix + "_country", String.class));
        entity.setStoreName(converter.fromRow(row, prefix + "_store_name", String.class));
        entity.setBusinessAccountNumber(converter.fromRow(row, prefix + "_business_account_number", Long.class));
        entity.setUserId(converter.fromRow(row, prefix + "_user_id", Long.class));
        return entity;
    }
}
