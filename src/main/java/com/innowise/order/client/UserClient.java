package com.innowise.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service", url = "${USER_SERVICE_URL}", fallback = UserClientFallback.class,
configuration = FeignClientConfig.class)
public interface UserClient {

    @GetMapping("/users/get/email")
    UserResponseDto getUserByEmail(@RequestParam String email);

    @GetMapping("/users/get/{id}")
    UserResponseDto getUserById(@PathVariable Long id);
}
