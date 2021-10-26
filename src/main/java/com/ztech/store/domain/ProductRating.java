package com.ztech.store.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A ProductRating.
 */
@Table("product_rating")
public class ProductRating implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @DecimalMin(value = "0")
    @DecimalMax(value = "5")
    @Column("value")
    private Float value;

    @NotNull(message = "must not be null")
    @Column("product_id")
    private Long productId;

    @NotNull(message = "must not be null")
    @Column("user_id")
    private Long userId;

    @Transient
    @JsonIgnoreProperties(value = { "productCategory" }, allowSetters = true)
    private Product productRating;

    @Transient
    private User rating;

    @Column("product_rating_id")
    private Long productRatingId;

    @Column("rating_id")
    private Long ratingId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ProductRating id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Float getValue() {
        return this.value;
    }

    public ProductRating value(Float value) {
        this.setValue(value);
        return this;
    }

    public void setValue(Float value) {
        this.value = value;
    }

    public Long getProductId() {
        return this.productId;
    }

    public ProductRating productId(Long productId) {
        this.setProductId(productId);
        return this;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getUserId() {
        return this.userId;
    }

    public ProductRating userId(Long userId) {
        this.setUserId(userId);
        return this;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Product getProductRating() {
        return this.productRating;
    }

    public void setProductRating(Product product) {
        this.productRating = product;
        this.productRatingId = product != null ? product.getId() : null;
    }

    public ProductRating productRating(Product product) {
        this.setProductRating(product);
        return this;
    }

    public User getRating() {
        return this.rating;
    }

    public void setRating(User user) {
        this.rating = user;
        this.ratingId = user != null ? user.getId() : null;
    }

    public ProductRating rating(User user) {
        this.setRating(user);
        return this;
    }

    public Long getProductRatingId() {
        return this.productRatingId;
    }

    public void setProductRatingId(Long product) {
        this.productRatingId = product;
    }

    public Long getRatingId() {
        return this.ratingId;
    }

    public void setRatingId(Long user) {
        this.ratingId = user;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProductRating)) {
            return false;
        }
        return id != null && id.equals(((ProductRating) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProductRating{" +
            "id=" + getId() +
            ", value=" + getValue() +
            ", productId=" + getProductId() +
            ", userId=" + getUserId() +
            "}";
    }
}
