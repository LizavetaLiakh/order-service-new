package com.innowise.order.controller;

import com.innowise.order.client.UserResponseDto;
import com.innowise.order.dto.OrderRequestDto;
import com.innowise.order.dto.OrderResponseDto;
import com.innowise.order.service.OrderService;
import com.innowise.order.status.Status;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST-controller for order management.
 * <p>
 * Provides CRUD-operations:
 * <ul>
 *     <li>Getting a list of orders by user email</li>
 *     <li>Getting user info by email</li>
 *     <li>Creating a new order</li>
 *     <li>Getting an order by id</li>
 *     <li>Getting a list of orders by their ids</li>
 *     <li>Updating an order by id</li>
 *     <li>Deleting an order by id</li>
 * </ul>
 */
@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    /**
     * Finds orders by e-mail.
     *
     * @param email User's e-mail.
     * @return A list of found orders.
     * @response 200 OK - Orders found.
     * @response 500 Internal Server Error - Unexpected server error occurred.
     */
    @PreAuthorize("@securityService.isOwnerOrAdminByEmail(#email)")
    @GetMapping("/get/email")
    public ResponseEntity<List<OrderResponseDto>> getOrdersByUserEmail(@RequestParam String email) {
        List<OrderResponseDto> orders = service.getOrdersByEmail(email);
        return ResponseEntity.status(HttpStatus.OK).body(orders);
    }

    /**
     * Finds a user by e-mail.
     *
     * @param email User's e-mail.
     * @return Found user.
     * @response 200 OK - User found.
     * @response 404 Not Found - User not found.
     * @response 500 Internal Server Error - Unexpected server error occurred.
     */
    @PreAuthorize("@securityService.isOwnerOrAdminByEmail(#email)")
    @GetMapping("/users/get/email")
    public ResponseEntity<UserResponseDto> getUserByEmail(@RequestParam String email) {
        UserResponseDto user = service.getUserByEmail(email);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    /**
     * Creates a new order.
     *
     * @param orderDto New order's data.
     * @return Created order.
     * @response 201 Created - New order successfully created.
     * @response 500 Internal Server Error - Unexpected server error occurred.
     */
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PostMapping("/add")
    public ResponseEntity<OrderResponseDto> addOrder(@RequestBody OrderRequestDto orderDto) {
        OrderResponseDto newOrder = service.createOrder(orderDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(newOrder);
    }

    /**
     * Finds an order by id.
     *
     * @param id Order's id.
     * @return Found order.
     * @throws com.innowise.order.exception.EntityNotFoundException If there's no order with given id.
     * @response 200 OK - Order found.
     * @response 404 Not Found - Order not found.
     * @response 500 Internal Server Error - Unexpected server error occurred.
     */
    @PreAuthorize("@securityService.isOrderOwnerOrAdmin(#id)")
    @GetMapping("/get/{id}")
    public ResponseEntity<OrderResponseDto> getOrderById(@PathVariable Long id) {
        OrderResponseDto order = service.getOrderById(id);
        return ResponseEntity.status(HttpStatus.OK).body(order);
    }

    /**
     * Finds orders by their ids.
     *
     * @param ids A list of orders' ids.
     * @return A list of found orders.
     * @throws com.innowise.order.exception.EmptyEntityListException If there's no orders with given ids.
     * @response 200 OK - Orders found.
     * @response 404 Not Found - Orders not found.
     * @response 500 Internal Server Error - Unexpected server error occurred.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/get")
    public ResponseEntity<List<OrderResponseDto>> getOrdersByIds(@RequestParam List<Long> ids) {
        List<OrderResponseDto> orders = service.getOrdersByIds(ids);
        return ResponseEntity.status(HttpStatus.OK).body(orders);
    }

    /**
     * Finds orders by status.
     *
     * @param status Some {@code Status} value.
     * @return A list of found orders.
     * @throws com.innowise.order.exception.OrdersWithStatusNotFoundException If there's no orders with given status.
     * @response 200 OK - Orders found.
     * @response 404 Not Found - Orders not found.
     * @response 500 Internal Server Error - Unexpected server error occurred.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/get/status")
    public ResponseEntity<List<OrderResponseDto>> getOrdersByStatus(@RequestParam String status) {
        List<OrderResponseDto> orders = service.getOrdersByStatus(Status.valueOf(status.toUpperCase()));
        return ResponseEntity.status(HttpStatus.OK).body(orders);
    }

    /**
     * Finds orders by user ID.
     *
     * @param userId User's unique identifier.
     * @return A list of found orders.
     * @throws com.innowise.order.exception.OrdersWithUserIdNotFoundException If there's no orders with given user ID.
     * @response 200 OK - Orders found.
     * @response 404 Not Found - Orders not found.
     * @response 500 Internal Server Error - Unexpected server error occurred.
     */
    @PreAuthorize("@securityService.isOwnerOrAdminByUserId(#userId)")
    @GetMapping("/get/user_id")
    public ResponseEntity<List<OrderResponseDto>> getOrdersByUserId(@RequestParam Long userId) {
        List<OrderResponseDto> orders = service.getOrdersByUserId(userId);
        return ResponseEntity.status(HttpStatus.OK).body(orders);
    }

    /**
     * Updates an order with given id.
     *
     * @param id Identifier of the order that should be updated.
     * @param orderDto New data of the order.
     * @throws com.innowise.order.exception.EntityNotFoundException If there's no order with given id.
     * @response 200 OK - Order successfully updated.
     * @response 404 Not Found - Order not found.
     * @response 500 Internal Server Error - Unexpected server error occurred.
     */
    @PreAuthorize("@securityService.isOrderOwnerOrAdmin(#id)")
    @PutMapping("/update/{id}")
    public ResponseEntity<OrderResponseDto> updateOrder(@PathVariable Long id,
                                                        @RequestBody OrderRequestDto orderDto) {
        OrderResponseDto updatedOrder = service.updateOrderById(id, orderDto);
        return ResponseEntity.status(HttpStatus.OK).body(updatedOrder);
    }

    /**
     * Deletes an order with given id.
     *
     * @param id Identifier of the order that should be deleted.
     * @throws com.innowise.order.exception.EntityNotFoundException If there's no order with given id.
     * @response 204 No Content - Order successfully deleted.
     * @response 404 Not Found - Order not found.
     * @response 500 Internal Server Error - Unexpected server error occurred.
     */
    @PreAuthorize("@securityService.isOrderOwnerOrAdmin(#id)")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        service.deleteOrderById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
