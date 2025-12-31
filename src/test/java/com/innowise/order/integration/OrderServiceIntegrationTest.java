package com.innowise.order.integration;

import com.innowise.order.client.UserClient;
import com.innowise.order.client.UserResponseDto;
import com.innowise.order.dto.OrderRequestDto;
import com.innowise.order.dto.OrderResponseDto;
import com.innowise.order.exception.EntityNotFoundException;
import com.innowise.order.repository.OrderRepository;
import com.innowise.order.service.OrderService;
import com.innowise.order.status.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OrderServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private OrderService service;

    @Autowired
    private OrderRepository repository;

    @Autowired
    private UserClient userClient;

    @BeforeEach
    void clean() {
        repository.deleteAll();
    }

    @Test
    void testCreateAndGetOrder() {
        OrderRequestDto orderRequestDto = new OrderRequestDto();
        orderRequestDto.setUserId(1L);
        orderRequestDto.setOrderStatus(OrderStatus.SHIPPED);
        orderRequestDto.setCreationDate(LocalDate.of(2025, 1, 10));

        OrderResponseDto createdOrder = service.createOrder(orderRequestDto);

        assertNotNull(createdOrder);
        assertEquals(OrderStatus.SHIPPED, createdOrder.getOrderStatus());
        assertEquals(LocalDate.of(2025, 1, 10), createdOrder.getCreationDate());

        OrderResponseDto foundOrder = service.getOrderById(createdOrder.getId());

        assertEquals(createdOrder.getId(), foundOrder.getId());
        assertEquals(createdOrder.getUser(), foundOrder.getUser());
        assertEquals(createdOrder.getOrderStatus(), foundOrder.getOrderStatus());
        assertEquals(createdOrder.getCreationDate(), foundOrder.getCreationDate());
    }

    @Test
    void testGetOrdersByEmail() {
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setId(1L);
        userResponseDto.setEmail("abc@gmail.com");
        userResponseDto.setName("Polly McDonald");

        OrderRequestDto orderRequestDto = new OrderRequestDto();
        orderRequestDto.setUserId(userResponseDto.getId());
        orderRequestDto.setOrderStatus(OrderStatus.SHIPPED);
        orderRequestDto.setCreationDate(LocalDate.of(2025, 10, 10));
        service.createOrder(orderRequestDto);

        OrderRequestDto order2 = new OrderRequestDto();
        order2.setUserId(userResponseDto.getId());
        order2.setOrderStatus(OrderStatus.PENDS_PAY);
        order2.setCreationDate(LocalDate.of(2025, 2, 10));
        service.createOrder(order2);

        List<OrderResponseDto> orders = service.getOrdersByEmail(userResponseDto.getEmail());

        assertNotNull(orders);
        assertEquals(2, orders.size());

        OrderResponseDto firstOrder = orders.get(0);
        assertEquals(userResponseDto.getId(), firstOrder.getUser().getId());
        assertEquals(userResponseDto.getEmail(), firstOrder.getUser().getEmail());
    }

    @Test
    void testUpdateOrderById() {
        OrderRequestDto orderRequestDto = new OrderRequestDto();
        orderRequestDto.setUserId(1L);
        orderRequestDto.setOrderStatus(OrderStatus.PENDS_PAY);
        orderRequestDto.setCreationDate(LocalDate.of(2025, 10, 10));

        OrderResponseDto createdOrderResponseDto = service.createOrder(orderRequestDto);

        assertNotNull(createdOrderResponseDto);
        assertEquals(OrderStatus.PENDS_PAY, createdOrderResponseDto.getOrderStatus());

        OrderRequestDto updateOrderRequestDto = new OrderRequestDto();
        updateOrderRequestDto.setUserId(1L);
        updateOrderRequestDto.setOrderStatus(OrderStatus.SHIPPED);
        updateOrderRequestDto.setCreationDate(LocalDate.of(2025, 4, 10));

        OrderResponseDto updatedOrderResponseDto = service.updateOrderById(createdOrderResponseDto.getId(), updateOrderRequestDto);

        assertNotNull(updatedOrderResponseDto);
        assertEquals(createdOrderResponseDto.getId(), updatedOrderResponseDto.getId());
        assertEquals(OrderStatus.SHIPPED, updatedOrderResponseDto.getOrderStatus());
        assertEquals(LocalDate.of(2025, 4, 10), updatedOrderResponseDto.getCreationDate());
        assertEquals(createdOrderResponseDto.getUser(), updatedOrderResponseDto.getUser());
    }

    @Test
    void testUpdateOrderByIdNotFound() {
        OrderRequestDto updateOrderRequestDto = new OrderRequestDto();
        updateOrderRequestDto.setUserId(1L);
        updateOrderRequestDto.setOrderStatus(OrderStatus.SHIPPED);
        updateOrderRequestDto.setCreationDate(LocalDate.of(2025, 4, 10));

        Long nonExistentId = 59L;

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                service.updateOrderById(nonExistentId, updateOrderRequestDto));

        assertEquals("Order with id " + nonExistentId + " not found", exception.getMessage());
    }

    @Test
    void testDeleteOrderById() {
        OrderRequestDto orderRequestDto = new OrderRequestDto();
        orderRequestDto.setUserId(1L);
        orderRequestDto.setOrderStatus(OrderStatus.SHIPPED);
        orderRequestDto.setCreationDate(LocalDate.of(2025, 5, 15));

        OrderResponseDto createdOrderResponseDto = service.createOrder(orderRequestDto);

        assertNotNull(createdOrderResponseDto);

        service.deleteOrderById(createdOrderResponseDto.getId());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () ->
                service.getOrderById(createdOrderResponseDto.getId()));

        assertEquals("Order with id " + createdOrderResponseDto.getId() + " not found", ex.getMessage());
    }
}