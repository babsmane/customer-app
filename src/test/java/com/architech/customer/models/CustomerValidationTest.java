package com.architech.customer.models;


import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CustomerValidationTest {


        private static Validator validator;

        @BeforeAll
        static void setUp() {
            ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
            validator = factory.getValidator();
        }

        @Test
        @DisplayName("Should validate customer with valid data")
        void shouldValidateCustomerWithValidData() {
            // Given
            Customer customer = new Customer(1L, "Valid Name", "valid@example.com");

            // When
            var violations = validator.validate(customer);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should allow customer with null name")
        void shouldAllowCustomerWithNullName() {
            // Given
            Customer customer = new Customer(1L, null, "email@example.com");

            // When
            var violations = validator.validate(customer);

            // Then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("Should allow customer with null email")
        void shouldAllowCustomerWithNullEmail() {
            // Given
            Customer customer = new Customer(1L, "Name", null);

            // When
            var violations = validator.validate(customer);

            // Then
            assertThat(violations).isEmpty();
        }
}
