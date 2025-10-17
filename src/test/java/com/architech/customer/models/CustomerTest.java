package com.architech.customer.models;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

import static org.assertj.core.api.Assertions.assertThat;
public class CustomerTest {

    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = new Customer();
    }

    @Test
    @DisplayName("Should create customer with no-args constructor")
    void shouldCreateCustomerWithNoArgsConstructor() {
        // Given - customer created in setUp()

        // Then
        assertThat(customer).isNotNull();
        assertThat(customer.getId()).isNull();
        assertThat(customer.getName()).isNull();
        assertThat(customer.getEmail()).isNull();
    }

    @Test
    @DisplayName("Should create customer with all-args constructor")
    void shouldCreateCustomerWithAllArgsConstructor() {
        // Given
        Long id = 1L;
        String name = "John Doe";
        String email = "john.doe@example.com";

        // When
        Customer customer = new Customer(id, name, email);

        // Then
        assertThat(customer).isNotNull();
        assertThat(customer.getId()).isEqualTo(id);
        assertThat(customer.getName()).isEqualTo(name);
        assertThat(customer.getEmail()).isEqualTo(email);
    }

    @Test
    @DisplayName("Should set and get id")
    void shouldSetAndGetId() {
        // Given
        Long id = 123L;

        // When
        customer.setId(id);

        // Then
        assertThat(customer.getId()).isEqualTo(id);
    }

    @Test
    @DisplayName("Should set and get name")
    void shouldSetAndGetName() {
        // Given
        String name = "Jane Smith";

        // When
        customer.setName(name);

        // Then
        assertThat(customer.getName()).isEqualTo(name);
    }

    @Test
    @DisplayName("Should set and get email")
    void shouldSetAndGetEmail() {
        // Given
        String email = "jane.smith@example.com";

        // When
        customer.setEmail(email);

        // Then
        assertThat(customer.getEmail()).isEqualTo(email);
    }

    @Test
    @DisplayName("Should generate equals and hashCode correctly")
    void shouldGenerateEqualsAndHashCodeCorrectly() {
        // Given
        Customer customer1 = new Customer(1L, "John Doe", "john@example.com");
        Customer customer2 = new Customer(1L, "John Doe", "john@example.com");
        Customer customer3 = new Customer(2L, "Jane Smith", "jane@example.com");

        // Then
        assertThat(customer1).isEqualTo(customer2);
        assertThat(customer1).isNotEqualTo(customer3);
        assertThat(customer1.hashCode()).isEqualTo(customer2.hashCode());
        assertThat(customer1.hashCode()).isNotEqualTo(customer3.hashCode());
    }

    @Test
    @DisplayName("Should generate toString correctly")
    void shouldGenerateToStringCorrectly() {
        // Given
        Customer customer = new Customer(1L, "John Doe", "john@example.com");

        // When
        String toString = customer.toString();

        // Then
        assertThat(toString).contains("id=1");
        assertThat(toString).contains("name=John Doe");
        assertThat(toString).contains("email=john@example.com");
    }

    @Test
    @DisplayName("Two customers with same id should be equal")
    void twoCustomersWithSameIdShouldBeEqual() {
        // Given
        Customer customer1 = new Customer(1L, "John Doe", "john@example.com");
        Customer customer2 = new Customer(1L, "Different Name", "different@example.com");

        // Then - Only ID matters for equality with @Data
        assertThat(customer1).isEqualTo(customer2);
    }

    @Test
    @DisplayName("Customer with null id should not equal customer with id")
    void customerWithNullIdShouldNotEqualCustomerWithId() {
        // Given
        Customer customer1 = new Customer(null, "John Doe", "john@example.com");
        Customer customer2 = new Customer(1L, "John Doe", "john@example.com");

        // Then
        assertThat(customer1).isNotEqualTo(customer2);
    }
}
