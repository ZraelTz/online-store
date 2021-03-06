package com.ztech.store.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ztech.store.domain.enumeration.ProductSize;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Product.
 */
@Table("product")
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("name")
    private String name;

    @Column("material")
    private String material;

    @Column("description")
    private String description;

    @NotNull(message = "must not be null")
    @DecimalMin(value = "0")
    @Column("price")
    private BigDecimal price;

    @NotNull(message = "must not be null")
    @Column("product_size")
    private ProductSize productSize;

    @Column("image")
    private byte[] image;

    @Column("image_content_type")
    private String imageContentType;

    @Transient
    @JsonIgnoreProperties(value = { "products" }, allowSetters = true)
    private ProductCategory productCategory;

    @Transient
    @JsonIgnoreProperties(value = { "user", "product" }, allowSetters = true)
    private Set<ProductRating> ratings = new HashSet<>();

    @Column("product_category_id")
    private Long productCategoryId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Product id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Product name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMaterial() {
        return this.material;
    }

    public Product material(String material) {
        this.setMaterial(material);
        return this;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getDescription() {
        return this.description;
    }

    public Product description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return this.price;
    }

    public Product price(BigDecimal price) {
        this.setPrice(price);
        return this;
    }

    public void setPrice(BigDecimal price) {
        this.price = price != null ? price.stripTrailingZeros() : null;
    }

    public ProductSize getProductSize() {
        return this.productSize;
    }

    public Product productSize(ProductSize productSize) {
        this.setProductSize(productSize);
        return this;
    }

    public void setProductSize(ProductSize productSize) {
        this.productSize = productSize;
    }

    public byte[] getImage() {
        return this.image;
    }

    public Product image(byte[] image) {
        this.setImage(image);
        return this;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getImageContentType() {
        return this.imageContentType;
    }

    public Product imageContentType(String imageContentType) {
        this.imageContentType = imageContentType;
        return this;
    }

    public void setImageContentType(String imageContentType) {
        this.imageContentType = imageContentType;
    }

    public ProductCategory getProductCategory() {
        return this.productCategory;
    }

    public void setProductCategory(ProductCategory productCategory) {
        this.productCategory = productCategory;
        this.productCategoryId = productCategory != null ? productCategory.getId() : null;
    }

    public Product productCategory(ProductCategory productCategory) {
        this.setProductCategory(productCategory);
        return this;
    }

    public Set<ProductRating> getRatings() {
        return this.ratings;
    }

    public void setRatings(Set<ProductRating> productRatings) {
        if (this.ratings != null) {
            this.ratings.forEach(i -> i.setProduct(null));
        }
        if (productRatings != null) {
            productRatings.forEach(i -> i.setProduct(this));
        }
        this.ratings = productRatings;
    }

    public Product ratings(Set<ProductRating> productRatings) {
        this.setRatings(productRatings);
        return this;
    }

    public Product addRating(ProductRating productRating) {
        this.ratings.add(productRating);
        productRating.setProduct(this);
        return this;
    }

    public Product removeRating(ProductRating productRating) {
        this.ratings.remove(productRating);
        productRating.setProduct(null);
        return this;
    }

    public Long getProductCategoryId() {
        return this.productCategoryId;
    }

    public void setProductCategoryId(Long productCategory) {
        this.productCategoryId = productCategory;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Product)) {
            return false;
        }
        return id != null && id.equals(((Product) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Product{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", material='" + getMaterial() + "'" +
            ", description='" + getDescription() + "'" +
            ", price=" + getPrice() +
            ", productSize='" + getProductSize() + "'" +
            ", image='" + getImage() + "'" +
            ", imageContentType='" + getImageContentType() + "'" +
            "}";
    }
}
