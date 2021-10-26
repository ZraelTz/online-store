package com.ztech.store.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.ztech.store.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProductRatingTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProductRating.class);
        ProductRating productRating1 = new ProductRating();
        productRating1.setId(1L);
        ProductRating productRating2 = new ProductRating();
        productRating2.setId(productRating1.getId());
        assertThat(productRating1).isEqualTo(productRating2);
        productRating2.setId(2L);
        assertThat(productRating1).isNotEqualTo(productRating2);
        productRating1.setId(null);
        assertThat(productRating1).isNotEqualTo(productRating2);
    }
}
