package com.architech.customer.controllers;


import com.architech.customer.models.Customer;
import com.architech.customer.repositories.CustomerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(CustomerController.class)
public class CustomerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CustomerRepository customerRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Customer customer1;
    private Customer customer2;

    @BeforeEach
    void setUp() {
        customer1 = new Customer(1L, "John Doe", "john.doe@example.com");
        customer2 = new Customer(2L, "Jane Smith", "jane.smith@example.com");
    }

    @Test
    @DisplayName("GET /customers - Should return all customers")
    void shouldReturnAllCustomers() throws Exception {
        // Given
        List<Customer> customers = Arrays.asList(customer1, customer2);
        given(customerRepository.findAll()).willReturn(customers);

        // When & Then
        mockMvc.perform(get("/customers")
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].id", is(1)))
            .andExpect(jsonPath("$[0].name", is("John Doe")))
            .andExpect(jsonPath("$[0].email", is("john.doe@example.com")))
            .andExpect(jsonPath("$[1].id", is(2)))
            .andExpect(jsonPath("$[1].name", is("Jane Smith")))
            .andExpect(jsonPath("$[1].email", is("jane.smith@example.com")));

        verify(customerRepository).findAll();
    }

    @Test
    @DisplayName("GET /customers/{id} - Should return customer by id")
    void shouldReturnCustomerById() throws Exception {
        // Given
        given(customerRepository.findById(1L)).willReturn(Optional.of(customer1));

        // When & Then
        mockMvc.perform(get("/customers/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.name", is("John Doe")))
            .andExpect(jsonPath("$.email", is("john.doe@example.com")));

        verify(customerRepository).findById(1L);
    }

    @Test
    @DisplayName("GET /customers/{id} - Should return 404 when customer not found")
    void shouldReturn404WhenCustomerNotFound() throws Exception {
        // Given
        given(customerRepository.findById(999L)).willReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/customers/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isNotFound());

        verify(customerRepository).findById(999L);
    }

    @Test
    @DisplayName("POST /customers - Should create new customer")
    void shouldCreateNewCustomer() throws Exception {
        // Given
        Customer newCustomer = new Customer(null, "New Customer", "new@example.com");
        Customer savedCustomer = new Customer(3L, "New Customer", "new@example.com");

        given(customerRepository.save(any(Customer.class))).willReturn(savedCustomer);

        // When & Then
        mockMvc.perform(post("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newCustomer)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(3)))
            .andExpect(jsonPath("$.name", is("New Customer")))
            .andExpect(jsonPath("$.email", is("new@example.com")));

        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    @DisplayName("POST /customers - Should handle invalid customer data")
    void shouldHandleInvalidCustomerData() throws Exception {
        // Given - Customer with null name (if you had validation)
        String invalidCustomerJson = "{\"name\":null,\"email\":\"test@example.com\"}";

        // When & Then
        mockMvc.perform(post("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidCustomerJson))
            .andDo(print())
            .andExpect(status().isOk()); // or isBadRequest() if you add validation

        // Repository might still be called depending on your validation setup
    }

    @Test
    @DisplayName("DELETE /customers/{id} - Should delete customer")
    void shouldDeleteCustomer() throws Exception {
        // Given
        Long customerId = 1L;
        doNothing().when(customerRepository).deleteById(customerId);

        // When & Then
        mockMvc.perform(delete("/customers/{id}", customerId))
            .andDo(print())
            .andExpect(status().isOk());

        verify(customerRepository).deleteById(customerId);
    }

    @Test
    @DisplayName("DELETE /customers/{id} - Should handle delete for non-existent customer")
    void shouldHandleDeleteForNonExistentCustomer() throws Exception {
        // Given
        Long customerId = 999L;
        doNothing().when(customerRepository).deleteById(customerId);

        // When & Then
        mockMvc.perform(delete("/customers/{id}", customerId))
            .andDo(print())
            .andExpect(status().isOk());

        verify(customerRepository).deleteById(customerId);
    }

    @Test
    @DisplayName("GET /customers/{id} - Should return 404 with custom message when customer not found")
    void shouldReturn404WithCustomMessageWhenCustomerNotFound() throws Exception {
        // Given
        given(customerRepository.findById(999L)).willReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/customers/{id}", 999L)
                .contentType(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(content().string("Customer not found with id: 999"));

        verify(customerRepository).findById(999L);
    }
}
