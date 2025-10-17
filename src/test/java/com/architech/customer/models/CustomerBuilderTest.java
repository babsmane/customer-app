package com.architech.customer.models;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class CustomerBuilderTest {

    @Test
    @DisplayName("Should build customer using builder pattern")
    void shouldBuildCustomerUsingBuilderPattern() {
        // Given & When
        Customer customer = Customer.builder()
            .id(1L)
            .name("Builder Test")
            .email("builder@example.com")
            .build();

        // Then
        assertThat(customer.getId()).isEqualTo(1L);
        assertThat(customer.getName()).isEqualTo("Builder Test");
        assertThat(customer.getEmail()).isEqualTo("builder@example.com");
    }
}
