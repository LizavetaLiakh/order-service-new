package com.innowise.order.controller;

import com.innowise.order.dto.OrderItemRequestDto;
import com.innowise.order.dto.OrderItemResponseDto;
import com.innowise.order.service.OrderItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order-items")
public class OrderItemController {

    private final OrderItemService service;

    public OrderItemController(OrderItemService service) {
        this.service = service;
    }

    @PostMapping("/add")
    public ResponseEntity<OrderItemResponseDto> addOrderItem(@RequestBody OrderItemRequestDto orderItemDto) {
        OrderItemResponseDto newOrderItem = service.createOrderItem(orderItemDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(newOrderItem);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<OrderItemResponseDto> getOrderItemById(@PathVariable Long id) {
        OrderItemResponseDto orderItem = service.getOrderItemById(id);
        return ResponseEntity.status(HttpStatus.OK).body(orderItem);
    }

    @GetMapping("/gat")
    public ResponseEntity<List<OrderItemResponseDto>> getOrderItemsByIds(@RequestParam List<Long> ids) {
        List<OrderItemResponseDto> orderItems = service.getOrderItemsByIds(ids);
        return ResponseEntity.status(HttpStatus.OK).body(orderItems);
    }

    @GetMapping("/get/order-id/{orderId}")
    public ResponseEntity<List<OrderItemResponseDto>> getOrderItemsByOrderId(@PathVariable Long orderId) {
        List<OrderItemResponseDto> orderItems = service.getOrderItemsByOrderId(orderId);
        return ResponseEntity.status(HttpStatus.OK).body(orderItems);
    }

    @GetMapping("/get/item-id/{itemId}")
    public ResponseEntity<List<OrderItemResponseDto>> getOrderItemsByItemId(@PathVariable Long itemId) {
        List<OrderItemResponseDto> orderItems = service.getOrderItemsByItemId(itemId);
        return ResponseEntity.status(HttpStatus.OK).body(orderItems);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<OrderItemResponseDto> updateOrderItem(@PathVariable Long id,
                                                                @RequestBody OrderItemRequestDto orderItemDto) {
        OrderItemResponseDto updatedOrderItem = service.updateOrderItemById(id, orderItemDto);
        return ResponseEntity.status(HttpStatus.OK).body(updatedOrderItem);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteOrderItem(@PathVariable Long id) {
        service.deleteOrderItemById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
