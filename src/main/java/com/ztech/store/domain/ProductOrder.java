package com.ztech.store.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ztech.store.domain.enumeration.OrderStatus;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A ProductOrder.
 */
@Table("product_order")
public class ProductOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("placed_date")
    private Instant placedDate;

    @NotNull(message = "must not be null")
    @Column("status")
    private OrderStatus status;

    @NotNull(message = "must not be null")
    @Column("code")
    private String code;

    @Transient
    @JsonIgnoreProperties(value = { "shipments", "productOrder" }, allowSetters = true)
    private Set<Invoice> invoices = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "product", "productOrder" }, allowSetters = true)
    private Set<OrderedItem> orderedItems = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "user", "orders" }, allowSetters = true)
    private Customer customer;

    @Column("customer_id")
    private Long customerId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ProductOrder id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getPlacedDate() {
        return this.placedDate;
    }

    public ProductOrder placedDate(Instant placedDate) {
        this.setPlacedDate(placedDate);
        return this;
    }

    public void setPlacedDate(Instant placedDate) {
        this.placedDate = placedDate;
    }

    public OrderStatus getStatus() {
        return this.status;
    }

    public ProductOrder status(OrderStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public String getCode() {
        return this.code;
    }

    public ProductOrder code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Set<Invoice> getInvoices() {
        return this.invoices;
    }

    public void setInvoices(Set<Invoice> invoices) {
        if (this.invoices != null) {
            this.invoices.forEach(i -> i.setProductOrder(null));
        }
        if (invoices != null) {
            invoices.forEach(i -> i.setProductOrder(this));
        }
        this.invoices = invoices;
    }

    public ProductOrder invoices(Set<Invoice> invoices) {
        this.setInvoices(invoices);
        return this;
    }

    public ProductOrder addInvoice(Invoice invoice) {
        this.invoices.add(invoice);
        invoice.setProductOrder(this);
        return this;
    }

    public ProductOrder removeInvoice(Invoice invoice) {
        this.invoices.remove(invoice);
        invoice.setProductOrder(null);
        return this;
    }

    public Set<OrderedItem> getOrderedItems() {
        return this.orderedItems;
    }

    public void setOrderedItems(Set<OrderedItem> orderedItems) {
        if (this.orderedItems != null) {
            this.orderedItems.forEach(i -> i.setProductOrder(null));
        }
        if (orderedItems != null) {
            orderedItems.forEach(i -> i.setProductOrder(this));
        }
        this.orderedItems = orderedItems;
    }

    public ProductOrder orderedItems(Set<OrderedItem> orderedItems) {
        this.setOrderedItems(orderedItems);
        return this;
    }

    public ProductOrder addOrderedItem(OrderedItem orderedItem) {
        this.orderedItems.add(orderedItem);
        orderedItem.setProductOrder(this);
        return this;
    }

    public ProductOrder removeOrderedItem(OrderedItem orderedItem) {
        this.orderedItems.remove(orderedItem);
        orderedItem.setProductOrder(null);
        return this;
    }

    public Customer getCustomer() {
        return this.customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
        this.customerId = customer != null ? customer.getId() : null;
    }

    public ProductOrder customer(Customer customer) {
        this.setCustomer(customer);
        return this;
    }

    public Long getCustomerId() {
        return this.customerId;
    }

    public void setCustomerId(Long customer) {
        this.customerId = customer;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProductOrder)) {
            return false;
        }
        return id != null && id.equals(((ProductOrder) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProductOrder{" +
            "id=" + getId() +
            ", placedDate='" + getPlacedDate() + "'" +
            ", status='" + getStatus() + "'" +
            ", code='" + getCode() + "'" +
            "}";
    }
}
