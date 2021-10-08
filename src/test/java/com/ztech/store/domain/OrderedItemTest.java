package com.ztech.store.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.ztech.store.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class OrderedItemTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(OrderedItem.class);
        OrderedItem orderedItem1 = new OrderedItem();
        orderedItem1.setId(1L);
        OrderedItem orderedItem2 = new OrderedItem();
        orderedItem2.setId(orderedItem1.getId());
        assertThat(orderedItem1).isEqualTo(orderedItem2);
        orderedItem2.setId(2L);
        assertThat(orderedItem1).isNotEqualTo(orderedItem2);
        orderedItem1.setId(null);
        assertThat(orderedItem1).isNotEqualTo(orderedItem2);
    }
}
