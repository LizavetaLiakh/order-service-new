package com.innowise.order.client;

import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class UserClientFallback implements UserClient {

    @Override
    public UserResponseDto getUserByEmail(String email) {
        UserResponseDto fallbackUser = new UserResponseDto();
        fallbackUser.setId(null);
        fallbackUser.setName("Unknown");
        fallbackUser.setSurname("User");
        fallbackUser.setBirthDate(null);
        fallbackUser.setEmail(email);
        return fallbackUser;
    }
}
