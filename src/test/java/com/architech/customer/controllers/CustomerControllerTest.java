package com.architech.customer.controllers;


import com.architech.customer.models.Customer;
import com.architech.customer.repositories.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class CustomerControllerTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerController customerController;

    private Customer customer1;
    private Customer customer2;

    @BeforeEach
    void setUp() {
        customer1 = new Customer(1L, "John Doe", "john.doe@example.com");
        customer2 = new Customer(2L, "Jane Smith", "jane.smith@example.com");
    }

    @Test
    @DisplayName("Should return all customers")
    void shouldReturnAllCustomers() {
        // Given
        List<Customer> customers = Arrays.asList(customer1, customer2);
        given(customerRepository.findAll()).willReturn(customers);

        // When
        List<Customer> result = customerController.getAllCustomers();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(customer1, customer2);
        verify(customerRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no customers exist")
    void shouldReturnEmptyListWhenNoCustomersExist() {
        // Given
        given(customerRepository.findAll()).willReturn(List.of());

        // When
        List<Customer> result = customerController.getAllCustomers();

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        verify(customerRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return customer by id")
    void shouldReturnCustomerById() {
        // Given
        Long customerId = 1L;
        given(customerRepository.findById(customerId)).willReturn(Optional.of(customer1));

        // When
        Customer result = customerController.getCustomerById(customerId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(customerId);
        assertThat(result.getName()).isEqualTo("John Doe");
        assertThat(result.getEmail()).isEqualTo("john.doe@example.com");
        verify(customerRepository, times(1)).findById(customerId);
    }

    @Test
    @DisplayName("Should throw exception when customer not found by id")
    void shouldThrowExceptionWhenCustomerNotFoundById() {
        // Given
        Long customerId = 999L;
        given(customerRepository.findById(customerId)).willReturn(Optional.empty());

        // When & Then - Update to match the actual exception type and message
        assertThatThrownBy(() -> customerController.getCustomerById(customerId))
            .isInstanceOf(CustomerController.CustomerNotFoundException.class) // Use the actual exception class
            .hasMessage("Customer not found with id: 999"); // Match the actual message

        verify(customerRepository, times(1)).findById(customerId);
    }

    @Test
    @DisplayName("Should add new customer")
    void shouldAddNewCustomer() {
        // Given
        Customer newCustomer = new Customer(null, "New Customer", "new@example.com");
        Customer savedCustomer = new Customer(3L, "New Customer", "new@example.com");

        given(customerRepository.save(newCustomer)).willReturn(savedCustomer);

        // When
        Customer result = customerController.addCustomer(newCustomer);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(3L);
        assertThat(result.getName()).isEqualTo("New Customer");
        assertThat(result.getEmail()).isEqualTo("new@example.com");
        verify(customerRepository, times(1)).save(newCustomer);
    }

    @Test
    @DisplayName("Should add customer with existing id (update scenario)")
    void shouldAddCustomerWithExistingId() {
        // Given
        Customer existingCustomer = new Customer(1L, "Updated Name", "updated@example.com");
        given(customerRepository.save(existingCustomer)).willReturn(existingCustomer);

        // When
        Customer result = customerController.addCustomer(existingCustomer);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Updated Name");
        verify(customerRepository, times(1)).save(existingCustomer);
    }

    @Test
    @DisplayName("Should delete customer by id")
    void shouldDeleteCustomerById() {
        // Given
        Long customerId = 1L;
        doNothing().when(customerRepository).deleteById(customerId);

        // When
        customerController.deleteCustomer(customerId);

        // Then
        verify(customerRepository, times(1)).deleteById(customerId);
    }

    @Test
    @DisplayName("Should handle delete for non-existent customer")
    void shouldHandleDeleteForNonExistentCustomer() {
        // Given
        Long customerId = 999L;
        doNothing().when(customerRepository).deleteById(customerId);

        // When & Then - Should not throw exception for non-existent ID
        customerController.deleteCustomer(customerId);

        // Then
        verify(customerRepository, times(1)).deleteById(customerId);
    }
}
