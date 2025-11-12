package com.innowise.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service", url = "${user-service.url}", fallback = UserClientFallback.class)
public interface UserClient {

    @GetMapping("/users/get/email")
    UserResponseDto getUserByEmail(@RequestParam String email);
}
