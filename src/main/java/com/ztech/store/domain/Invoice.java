package com.ztech.store.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ztech.store.domain.enumeration.InvoiceStatus;
import com.ztech.store.domain.enumeration.PaymentMethod;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Invoice.
 */
@Table("invoice")
public class Invoice implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("date")
    private Instant date;

    @Column("details")
    private String details;

    @NotNull(message = "must not be null")
    @Column("status")
    private InvoiceStatus status;

    @NotNull(message = "must not be null")
    @Column("payment_method")
    private PaymentMethod paymentMethod;

    @NotNull(message = "must not be null")
    @Column("payment_date")
    private Instant paymentDate;

    @NotNull(message = "must not be null")
    @Column("payment_amount")
    private BigDecimal paymentAmount;

    @Column("code")
    private String code;

    @Transient
    @JsonIgnoreProperties(value = { "invoice" }, allowSetters = true)
    private Set<Shipment> shipments = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "invoices", "orderedItems", "customer" }, allowSetters = true)
    private ProductOrder productOrder;

    @Column("product_order_id")
    private Long productOrderId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Invoice id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getDate() {
        return this.date;
    }

    public Invoice date(Instant date) {
        this.setDate(date);
        return this;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public String getDetails() {
        return this.details;
    }

    public Invoice details(String details) {
        this.setDetails(details);
        return this;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public InvoiceStatus getStatus() {
        return this.status;
    }

    public Invoice status(InvoiceStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(InvoiceStatus status) {
        this.status = status;
    }

    public PaymentMethod getPaymentMethod() {
        return this.paymentMethod;
    }

    public Invoice paymentMethod(PaymentMethod paymentMethod) {
        this.setPaymentMethod(paymentMethod);
        return this;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Instant getPaymentDate() {
        return this.paymentDate;
    }

    public Invoice paymentDate(Instant paymentDate) {
        this.setPaymentDate(paymentDate);
        return this;
    }

    public void setPaymentDate(Instant paymentDate) {
        this.paymentDate = paymentDate;
    }

    public BigDecimal getPaymentAmount() {
        return this.paymentAmount;
    }

    public Invoice paymentAmount(BigDecimal paymentAmount) {
        this.setPaymentAmount(paymentAmount);
        return this;
    }

    public void setPaymentAmount(BigDecimal paymentAmount) {
        this.paymentAmount = paymentAmount != null ? paymentAmount.stripTrailingZeros() : null;
    }

    public String getCode() {
        return this.code;
    }

    public Invoice code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Set<Shipment> getShipments() {
        return this.shipments;
    }

    public void setShipments(Set<Shipment> shipments) {
        if (this.shipments != null) {
            this.shipments.forEach(i -> i.setInvoice(null));
        }
        if (shipments != null) {
            shipments.forEach(i -> i.setInvoice(this));
        }
        this.shipments = shipments;
    }

    public Invoice shipments(Set<Shipment> shipments) {
        this.setShipments(shipments);
        return this;
    }

    public Invoice addShipment(Shipment shipment) {
        this.shipments.add(shipment);
        shipment.setInvoice(this);
        return this;
    }

    public Invoice removeShipment(Shipment shipment) {
        this.shipments.remove(shipment);
        shipment.setInvoice(null);
        return this;
    }

    public ProductOrder getProductOrder() {
        return this.productOrder;
    }

    public void setProductOrder(ProductOrder productOrder) {
        this.productOrder = productOrder;
        this.productOrderId = productOrder != null ? productOrder.getId() : null;
    }

    public Invoice productOrder(ProductOrder productOrder) {
        this.setProductOrder(productOrder);
        return this;
    }

    public Long getProductOrderId() {
        return this.productOrderId;
    }

    public void setProductOrderId(Long productOrder) {
        this.productOrderId = productOrder;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Invoice)) {
            return false;
        }
        return id != null && id.equals(((Invoice) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Invoice{" +
            "id=" + getId() +
            ", date='" + getDate() + "'" +
            ", details='" + getDetails() + "'" +
            ", status='" + getStatus() + "'" +
            ", paymentMethod='" + getPaymentMethod() + "'" +
            ", paymentDate='" + getPaymentDate() + "'" +
            ", paymentAmount=" + getPaymentAmount() +
            ", code='" + getCode() + "'" +
            "}";
    }
}
