package com.innowise.order.controller;

import com.innowise.order.dto.OrderItemRequestDto;
import com.innowise.order.dto.OrderItemResponseDto;
import com.innowise.order.service.OrderItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST-controller for order item management.
 * <p>
 * Provides CRUD-operations:
 * <ul>
 *     <li>Creating a new order item</li>
 *     <li>Getting an order item by id</li>
 *     <li>Getting a list of order items by their ids</li>
 *     <li>Getting a list of order items by order id</li>
 *     <li>Getting a list of order items by item id</li>
 *     <li>Updating an item by id</li>
 *     <li>Deleting an item by id</li>
 * </ul>
 */
@RestController
@RequestMapping("/order-items")
public class OrderItemController {

    private final OrderItemService service;

    public OrderItemController(OrderItemService service) {
        this.service = service;
    }

    /**
     * Creates a new order item.
     *
     * @param orderItemDto New order item's data.
     * @return Created order item.
     * @response 201 Created - New order item successfully created.
     * @response 500 Internal Server Error - Unexpected server error occurred.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add")
    public ResponseEntity<OrderItemResponseDto> addOrderItem(@RequestBody OrderItemRequestDto orderItemDto) {
        OrderItemResponseDto newOrderItem = service.createOrderItem(orderItemDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(newOrderItem);
    }

    /**
     * Finds an order item by id.
     *
     * @param id Order item's id.
     * @return Found order item.
     * @throws com.innowise.order.exception.EntityNotFoundException If there's no order item with given id.
     * @response 200 OK - Order item found.
     * @response 404 Not Found - Order item not found.
     * @response 500 Internal Server Error - Unexpected server error occurred.
     */
    @PreAuthorize("@securityService.isOrderItemOwnerOrAdmin(#id)")
    @GetMapping("/get/{id}")
    public ResponseEntity<OrderItemResponseDto> getOrderItemById(@PathVariable Long id) {
        OrderItemResponseDto orderItem = service.getOrderItemById(id);
        return ResponseEntity.status(HttpStatus.OK).body(orderItem);
    }

    /**
     * Finds order items by their ids.
     *
     * @param ids A list of order items' ids.
     * @return A list of found order items.
     * @throws com.innowise.order.exception.EmptyEntityListException If there's no order items with given ids.
     * @response 200 OK - Order items found.
     * @response 404 Not Found - Order items not found.
     * @response 500 Internal Server Error - Unexpected server error occurred.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/get")
    public ResponseEntity<List<OrderItemResponseDto>> getOrderItemsByIds(@RequestParam List<Long> ids) {
        List<OrderItemResponseDto> orderItems = service.getOrderItemsByIds(ids);
        return ResponseEntity.status(HttpStatus.OK).body(orderItems);
    }

    /**
     * Finds order items by order id.
     *
     * @param orderId Identifier of some order.
     * @return A list of found order items.
     * @throws com.innowise.order.exception.EmptyOrderItemListSingleIdException If there's no order items with given id.
     * @response 200 OK - Order items found.
     * @response 404 Not Found - Order items not found.
     * @response 500 Internal Server Error - Unexpected server error occurred.
     */
    @PreAuthorize("@securityService.isOrderOwnerOrAdmin(#orderId)")
    @GetMapping("/get/order-id/{orderId}")
    public ResponseEntity<List<OrderItemResponseDto>> getOrderItemsByOrderId(@PathVariable Long orderId) {
        List<OrderItemResponseDto> orderItems = service.getOrderItemsByOrderId(orderId);
        return ResponseEntity.status(HttpStatus.OK).body(orderItems);
    }

    /**
     * Finds order items by item id.
     *
     * @param itemId Identifier of some item.
     * @return A list of found order items.
     * @throws com.innowise.order.exception.EmptyOrderItemListSingleIdException If there's no order items with given id.
     * @response 200 OK - Order items found.
     * @response 404 Not Found - Order items not found.
     * @response 500 Internal Server Error - Unexpected server error occurred.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/get/item-id/{itemId}")
    public ResponseEntity<List<OrderItemResponseDto>> getOrderItemsByItemId(@PathVariable Long itemId) {
        List<OrderItemResponseDto> orderItems = service.getOrderItemsByItemId(itemId);
        return ResponseEntity.status(HttpStatus.OK).body(orderItems);
    }

    /**
     * Updates an order item with given id.
     *
     * @param id Identifier of the order item that should be updated.
     * @param orderItemDto New data of the order item.
     * @throws com.innowise.order.exception.EntityNotFoundException If there's no order item with given id.
     * @response 200 OK - Order item successfully updated.
     * @response 404 Not Found - Order item not found.
     * @response 500 Internal Server Error - Unexpected server error occurred.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update/{id}")
    public ResponseEntity<OrderItemResponseDto> updateOrderItem(@PathVariable Long id,
                                                                @RequestBody OrderItemRequestDto orderItemDto) {
        OrderItemResponseDto updatedOrderItem = service.updateOrderItemById(id, orderItemDto);
        return ResponseEntity.status(HttpStatus.OK).body(updatedOrderItem);
    }

    /**
     * Deletes an order item with given id.
     *
     * @param id Identifier of the order item that should be deleted.
     * @throws com.innowise.order.exception.EntityNotFoundException If there's no order item with given id.
     * @response 204 No Content - Order item successfully deleted.
     * @response 404 Not Found - Order item not found.
     * @response 500 Internal Server Error - Unexpected server error occurred.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteOrderItem(@PathVariable Long id) {
        service.deleteOrderItemById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
