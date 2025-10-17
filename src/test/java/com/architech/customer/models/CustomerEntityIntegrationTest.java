package com.architech.customer.models;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class CustomerEntityIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Should persist customer to database")
    void shouldPersistCustomerToDatabase() {
        // Given
        Customer customer = new Customer();
        customer.setName("Test Customer");
        customer.setEmail("test@example.com");

        // When
        Customer savedCustomer = entityManager.persistAndFlush(customer);

        // Then
        assertThat(savedCustomer.getId()).isNotNull();
        assertThat(savedCustomer.getName()).isEqualTo("Test Customer");
        assertThat(savedCustomer.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("Should retrieve customer from database")
    void shouldRetrieveCustomerFromDatabase() {
        // Given
        Customer customer = new Customer();
        customer.setName("Retrieve Test");
        customer.setEmail("retrieve@example.com");
        Customer savedCustomer = entityManager.persistAndFlush(customer);

        // When
        Customer foundCustomer = entityManager.find(Customer.class, savedCustomer.getId());

        // Then
        assertThat(foundCustomer).isNotNull();
        assertThat(foundCustomer.getId()).isEqualTo(savedCustomer.getId());
        assertThat(foundCustomer.getName()).isEqualTo("Retrieve Test");
        assertThat(foundCustomer.getEmail()).isEqualTo("retrieve@example.com");
    }

    @Test
    @DisplayName("Should update customer in database")
    void shouldUpdateCustomerInDatabase() {
        // Given
        Customer customer = new Customer();
        customer.setName("Original Name");
        customer.setEmail("original@example.com");
        Customer savedCustomer = entityManager.persistAndFlush(customer);

        // When
        savedCustomer.setName("Updated Name");
        savedCustomer.setEmail("updated@example.com");
        entityManager.persistAndFlush(savedCustomer);

        // Then
        Customer updatedCustomer = entityManager.find(Customer.class, savedCustomer.getId());
        assertThat(updatedCustomer.getName()).isEqualTo("Updated Name");
        assertThat(updatedCustomer.getEmail()).isEqualTo("updated@example.com");
    }

    @Test
    @DisplayName("Should delete customer from database")
    void shouldDeleteCustomerFromDatabase() {
        // Given
        Customer customer = new Customer();
        customer.setName("Delete Test");
        customer.setEmail("delete@example.com");
        Customer savedCustomer = entityManager.persistAndFlush(customer);

        // When
        entityManager.remove(savedCustomer);
        entityManager.flush();

        // Then
        Customer deletedCustomer = entityManager.find(Customer.class, savedCustomer.getId());
        assertThat(deletedCustomer).isNull();
    }
}
