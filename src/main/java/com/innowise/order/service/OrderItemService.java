package com.innowise.order.service;

import com.innowise.order.dto.OrderItemRequestDto;
import com.innowise.order.dto.OrderItemResponseDto;
import com.innowise.order.entity.OrderItem;
import com.innowise.order.exception.EmptyOrderItemListException;
import com.innowise.order.exception.EmptyOrderItemListSingleIdException;
import com.innowise.order.exception.OrderItemNotFoundException;
import com.innowise.order.mapper.OrderItemMapper;
import com.innowise.order.repository.OrderItemRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service class for managing OrderItem entity.
 * <p>
 *     Provides CRUD operations: create, get order item by id, get order items by ids, update order item by id,
 *     delete order item by id, get order items by order id or item id.
 * </p>
 */
@Service
public class OrderItemService {

    private final OrderItemRepository repository;
    private final OrderItemMapper mapper;

    public OrderItemService(OrderItemRepository repository, OrderItemMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * Creates a new order item in the database.
     * @param orderItemDto DTO with new order item's data
     * @return created order item as DTO
     */
    public OrderItemResponseDto createOrderItem(OrderItemRequestDto orderItemDto) {
        OrderItem orderItem = mapper.toOrderItem(orderItemDto);
        OrderItem savedOrderItem = repository.save(orderItem);
        return mapper.toOrderItemResponseDto(savedOrderItem);
    }

    /**
     * Finds an order item by id.
     * @param id order item's unique identifier
     * @return order item as DTO if found, empty if not found
     */
    public OrderItemResponseDto getOrderItemById(Long id) {
        return repository.findById(id)
                .map(mapper::toOrderItemResponseDto)
                .orElseThrow(() -> new OrderItemNotFoundException(id));
    }

    /**
     * Finds order items by ids.
     * @param ids list of order items' unique identifiers
     * @return list of order items as DTOs
     */
    public List<OrderItemResponseDto> getOrderItemsByIds(Iterable<Long> ids) {
        List<OrderItemResponseDto> orderItems = repository.findAllById(ids)
                .stream()
                .map(mapper::toOrderItemResponseDto)
                .toList();
        if (orderItems.isEmpty()) {
            throw new EmptyOrderItemListException(ids);
        }
        return orderItems;
    }

    /**
     * Finds order items by order id.
     * @param orderId order's unique identifier
     * @return list of order items as DTOs
     */
    public List<OrderItemResponseDto> getOrderItemsByOrderId(Long orderId) {
        List<OrderItemResponseDto> orderItems = repository.findByOrderId(orderId)
                .stream()
                .map(mapper::toOrderItemResponseDto)
                .toList();
        if (orderItems.isEmpty()) {
            throw new EmptyOrderItemListSingleIdException("order", orderId);
        }
        return orderItems;
    }

    /**
     * Finds order items by item id.
     * @param itemId item's unique identifier
     * @return list of order items as DTOs
     */
    public List<OrderItemResponseDto> getOrderItemsByItemId(Long itemId) {
        List<OrderItemResponseDto> orderItems = repository.findByItemId(itemId)
                .stream()
                .map(mapper::toOrderItemResponseDto)
                .toList();
        if (orderItems.isEmpty()) {
            throw new EmptyOrderItemListSingleIdException("item", itemId);
        }
        return orderItems;
    }

    /**
     * Updates an order item by id.
     * @param id order item's unique identifier
     * @param orderItemDto OrderItemRequestDto that contains new data
     */
    @Transactional
    public OrderItemResponseDto updateOrderItemById(Long id, OrderItemRequestDto orderItemDto) {
        int updated = repository.updateOrderItem(id, orderItemDto.getOrderId(), orderItemDto.getItemId(),
                orderItemDto.getQuantity());
        if (updated == 0) {
            throw new OrderItemNotFoundException(id);
        }
        return repository.findById(id)
                .map(mapper::toOrderItemResponseDto)
                .orElseThrow(() -> new OrderItemNotFoundException(id));
    }

    /**
     * Deletes an order item by id.
     * @param id order item's id
     */
    @Transactional
    public void deleteOrderItemById(Long id) {
        try {
            repository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new OrderItemNotFoundException(id);
        }
    }
}
