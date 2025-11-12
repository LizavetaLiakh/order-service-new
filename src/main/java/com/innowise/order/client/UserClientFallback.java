package com.innowise.order.client;

import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class UserClientFallback implements UserClient {

    @Override
    public UserResponseDto getUserByEmail(String email) {
        UserResponseDto fallbackUser = new UserResponseDto();
        fallbackUser.setId(0L);
        fallbackUser.setName("Unknown");
        fallbackUser.setSurname("User");
        fallbackUser.setBirthDate(LocalDate.of(1970, 1, 1));
        fallbackUser.setEmail(email);
        return fallbackUser;
    }

    @Override
    public UserResponseDto getUserById(Long id) {
        UserResponseDto fallbackUser = new UserResponseDto();
        fallbackUser.setId(id);
        fallbackUser.setName("Unknown");
        fallbackUser.setSurname("User");
        fallbackUser.setBirthDate(LocalDate.of(1970, 1, 1));
        fallbackUser.setEmail("unknown@user.service");
        return fallbackUser;
    }
}
