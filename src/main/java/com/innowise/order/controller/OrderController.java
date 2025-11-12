package com.innowise.order.controller;

import com.innowise.order.client.UserResponseDto;
import com.innowise.order.dto.OrderRequestDto;
import com.innowise.order.dto.OrderResponseDto;
import com.innowise.order.service.OrderService;
import com.innowise.order.status.Status;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @GetMapping("/get/email")
    public ResponseEntity<List<OrderResponseDto>> getOrdersByUserEmail(@RequestParam String email) {
        List<OrderResponseDto> orders = service.getOrdersByEmail(email);
        return ResponseEntity.status(HttpStatus.OK).body(orders);
    }

    @GetMapping("/users/get/email")
    public ResponseEntity<UserResponseDto> getUserByEmail(@RequestParam String email) {
        UserResponseDto user = service.getUserByEmail(email);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @PostMapping("/add")
    public ResponseEntity<OrderResponseDto> addOrder(@RequestBody OrderRequestDto orderDto) {
        OrderResponseDto newOrder = service.createOrder(orderDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(newOrder);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<OrderResponseDto> getOrderById(@PathVariable Long id) {
        OrderResponseDto order = service.getOrderById(id);
        return ResponseEntity.status(HttpStatus.OK).body(order);
    }

    @GetMapping("/get")
    public ResponseEntity<List<OrderResponseDto>> getOrdersByIds(@RequestParam List<Long> ids) {
        List<OrderResponseDto> orders = service.getOrdersByIds(ids);
        return ResponseEntity.status(HttpStatus.OK).body(orders);
    }

    @GetMapping("/get/status")
    public ResponseEntity<List<OrderResponseDto>> getOrdersByStatus(@RequestParam String status) {
        List<OrderResponseDto> orders = service.getOrdersByStatus(Status.valueOf(status.toUpperCase()));
        return ResponseEntity.status(HttpStatus.OK).body(orders);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<OrderResponseDto> updateOrder(@PathVariable Long id,
                                                        @RequestBody OrderRequestDto orderDto) {
        OrderResponseDto updatedOrder = service.updateOrderById(id, orderDto);
        return ResponseEntity.status(HttpStatus.OK).body(updatedOrder);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        service.deleteOrderById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
