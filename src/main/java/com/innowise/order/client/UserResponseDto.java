package com.innowise.order.client;

import lombok.Data;

import java.time.LocalDate;

/**
 * Data Transfer Object for returning user data in REST API responses from Order Service.
 */
@Data
public class UserResponseDto {

    private Long id;
    private String name;
    private String surname;
    public LocalDate birthDate;
    private String email;
}
