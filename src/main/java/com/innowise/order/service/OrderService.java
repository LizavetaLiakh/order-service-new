package com.innowise.order.service;

import com.innowise.order.client.UserClient;
import com.innowise.order.client.UserResponseDto;
import com.innowise.order.dto.OrderRequestDto;
import com.innowise.order.dto.OrderResponseDto;
import com.innowise.order.entity.Order;
import com.innowise.order.exception.EmptyOrderListException;
import com.innowise.order.exception.OrderNotFoundException;
import com.innowise.order.exception.OrdersWithStatusNotFoundException;
import com.innowise.order.mapper.OrderMapper;
import com.innowise.order.repository.OrderRepository;
import com.innowise.order.status.Status;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service class for managing Order entity.
 * <p>
 *     Provides CRUD operations: create, get order by id, get orders by ids, get orders by status, update order by id,
 *     delete order by id, get user's orders by email, get some user by email.
 * </p>
 */
@Service
public class OrderService {

    private final OrderRepository repository;
    private final UserClient userClient;
    private final OrderMapper mapper;

    public OrderService(OrderRepository orderRepository, UserClient userClient, OrderMapper orderMapper) {
        this.repository = orderRepository;
        this.userClient = userClient;
        this.mapper = orderMapper;
    }

    /**
     * Finds orders by user's email.
     * @param email user's email
     * @return list of orders
     */
    public List<OrderResponseDto> getOrdersByEmail(String email) {
        UserResponseDto userResponseDto = userClient.getUserByEmail(email);
        List<Order> userOrders = repository.findByUserId(userResponseDto.getId());
        return userOrders.stream()
                .map(order ->  getOrderResponseWithUser(order, userResponseDto))
                .toList();
    }

    /**
     * Finds a user by email.
     * @param email user's email
     * @return user info
     */
    public UserResponseDto getUserByEmail(String email) {
        return userClient.getUserByEmail(email);
    }

    /**
     * Creates a new order in the database.
     * @param orderDto DTO with new order's data
     * @return created order as DTO with user info
     */
    public OrderResponseDto createOrder(OrderRequestDto orderDto) {
        Order order = mapper.toOrder(orderDto);
        Order savedOrder = repository.save(order);
        return getOrderResponseWithUser(savedOrder, userClient.getUserById(order.getUserId()));
    }

    /**
     * Finds an order by id.
     * @param id order's unique identifier
     * @return order as DTO with user info if found, empty if not found
     */
    public OrderResponseDto getOrderById(Long id) {
        Order order = repository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        return getOrderResponseWithUser(order, userClient.getUserById(order.getUserId()));
    }

    /**
     * Finds orders by ids.
     * @param ids list of orders' unique identifiers
     * @return list of orders as DTOs
     */
    public List<OrderResponseDto> getOrdersByIds(Iterable<Long> ids) {
        List<OrderResponseDto> orders = repository.findAllById(ids)
                .stream()
                .map(order -> getOrderResponseWithUser(order, userClient.getUserById(order.getUserId())))
                .toList();
        if (orders.isEmpty()) {
            throw new EmptyOrderListException(ids);
        }
        return orders;
    }

    /**
     * Finds orders with some status.
     * @param status orders' status
     * @return list of orders as DTOs
     */
    public List<OrderResponseDto> getOrdersByStatus(Status status) {
        List<OrderResponseDto> orders = repository.findByStatus(status.name())
                .stream()
                .map(order -> getOrderResponseWithUser(order, userClient.getUserById(order.getUserId())))
                .toList();
        if (orders.isEmpty()) {
            throw new OrdersWithStatusNotFoundException(status.name());
        }
        return orders;
    }

    /**
     * Updates an order by id.
     * @param id order's unique identifier
     * @param orderDto OrderRequestDto that contains new data
     */
    @Transactional
    public OrderResponseDto updateOrderById(Long id, OrderRequestDto orderDto) {
        int updated = repository.updateOrder(id, orderDto.getUserId(), orderDto.getStatus().name(),
                orderDto.getCreationDate());
        if (updated == 0) {
            throw new OrderNotFoundException(id);
        }
        Order order = repository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        return getOrderResponseWithUser(order, userClient.getUserById(order.getUserId()));
    }

    /**
     * Deletes an order by id.
     * @param id order's id
     */
    @Transactional
    public void deleteOrderById(Long id) {
        try {
            repository.deleteById(id);
        } catch (EmptyResultDataAccessException  e) {
            throw new OrderNotFoundException(id);
        }
    }

    private OrderResponseDto getOrderResponseWithUser(Order order, UserResponseDto user) {
        OrderResponseDto orderResponseDto = mapper.toOrderResponseDto(order);
        orderResponseDto.setUser(user);
        return orderResponseDto;
    }
}
