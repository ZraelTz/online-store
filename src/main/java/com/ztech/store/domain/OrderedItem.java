package com.ztech.store.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ztech.store.domain.enumeration.OrderedItemStatus;
import java.io.Serializable;
import java.math.BigDecimal;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A OrderedItem.
 */
@Table("ordered_item")
public class OrderedItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Min(value = 0)
    @Column("quantity")
    private Integer quantity;

    @NotNull(message = "must not be null")
    @DecimalMin(value = "0")
    @Column("total_price")
    private BigDecimal totalPrice;

    @NotNull(message = "must not be null")
    @Column("status")
    private OrderedItemStatus status;

    @Transient
    @JsonIgnoreProperties(value = { "productCategory" }, allowSetters = true)
    private Product product;

    @Transient
    @JsonIgnoreProperties(value = { "invoices", "orderedItems", "customer" }, allowSetters = true)
    private ProductOrder order;

    @Column("product_id")
    private Long productId;

    @Column("order_id")
    private Long orderId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public OrderedItem id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantity() {
        return this.quantity;
    }

    public OrderedItem quantity(Integer quantity) {
        this.setQuantity(quantity);
        return this;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getTotalPrice() {
        return this.totalPrice;
    }

    public OrderedItem totalPrice(BigDecimal totalPrice) {
        this.setTotalPrice(totalPrice);
        return this;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice != null ? totalPrice.stripTrailingZeros() : null;
    }

    public OrderedItemStatus getStatus() {
        return this.status;
    }

    public OrderedItem status(OrderedItemStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(OrderedItemStatus status) {
        this.status = status;
    }

    public Product getProduct() {
        return this.product;
    }

    public void setProduct(Product product) {
        this.product = product;
        this.productId = product != null ? product.getId() : null;
    }

    public OrderedItem product(Product product) {
        this.setProduct(product);
        return this;
    }

    public ProductOrder getOrder() {
        return this.order;
    }

    public void setOrder(ProductOrder productOrder) {
        this.order = productOrder;
        this.orderId = productOrder != null ? productOrder.getId() : null;
    }

    public OrderedItem order(ProductOrder productOrder) {
        this.setOrder(productOrder);
        return this;
    }

    public Long getProductId() {
        return this.productId;
    }

    public void setProductId(Long product) {
        this.productId = product;
    }

    public Long getOrderId() {
        return this.orderId;
    }

    public void setOrderId(Long productOrder) {
        this.orderId = productOrder;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrderedItem)) {
            return false;
        }
        return id != null && id.equals(((OrderedItem) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OrderedItem{" +
            "id=" + getId() +
            ", quantity=" + getQuantity() +
            ", totalPrice=" + getTotalPrice() +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
