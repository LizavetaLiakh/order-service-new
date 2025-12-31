package com.innowise.order.integration;

import com.innowise.order.client.UserClient;
import com.innowise.order.dto.OrderRequestDto;
import com.innowise.order.dto.OrderResponseDto;
import com.innowise.order.repository.OrderRepository;
import com.innowise.order.service.OrderService;
import com.innowise.order.status.Status;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
public class OrderServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private OrderService service;

    @Autowired
    private OrderRepository repository;

    @Autowired
    private UserClient userClient;

    @Test
    void testCreateAndGetOrder() {
        OrderRequestDto orderRequestDto = new OrderRequestDto();
        orderRequestDto.setUserId(1L);
        orderRequestDto.setStatus(Status.SHIPPED);
        orderRequestDto.setCreationDate(LocalDate.of(2025, 1, 1));

        OrderResponseDto createdOrder = service.createOrder(orderRequestDto);

        assertNotNull(createdOrder);
        assertEquals(1L, createdOrder.getId());
        assertEquals(Status.SHIPPED, createdOrder.getStatus());
        assertEquals(LocalDate.of(2025, 1, 1), createdOrder.getCreationDate());

        OrderResponseDto foundOrder = service.getOrderById(createdOrder.getId());

        assertEquals(createdOrder.getId(), foundOrder.getId());
        assertEquals(createdOrder.getUser(), foundOrder.getUser()); // WireMock возвращает пользователя
        assertEquals(createdOrder.getStatus(), foundOrder.getStatus());
        assertEquals(createdOrder.getCreationDate(), foundOrder.getCreationDate());
    }
}