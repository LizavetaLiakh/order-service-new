package com.innowise.order.service;

import com.innowise.order.client.UserClient;
import com.innowise.order.client.UserResponseDto;
import com.innowise.order.dto.OrderEventDto;
import com.innowise.order.dto.OrderRequestDto;
import com.innowise.order.dto.OrderResponseDto;
import com.innowise.order.dto.PaymentEventDto;
import com.innowise.order.entity.Order;
import com.innowise.order.exception.EmptyEntityListException;
import com.innowise.order.exception.EntityNotFoundException;
import com.innowise.order.exception.OrdersWithStatusNotFoundException;
import com.innowise.order.exception.OrdersWithUserIdNotFoundException;
import com.innowise.order.kafka.OrderProducer;
import com.innowise.order.mapper.OrderMapper;
import com.innowise.order.repository.OrderRepository;
import com.innowise.order.status.OrderStatus;
import com.innowise.order.status.PaymentStatus;
import feign.FeignException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.kafka.annotation.KafkaListener;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

/**
 * Service class for managing Order entity.
 * <p>
 *     Provides CRUD operations: create, get order by id, get orders by ids, get orders by status, update order by id,
 *     delete order by id, get user's orders by email, get some user by email.
 * </p>
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class OrderService {

    private final OrderRepository repository;
    private final UserClient userClient;
    private final OrderMapper mapper;
    private final OrderProducer orderProducer;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public OrderService(OrderRepository orderRepository, UserClient userClient, OrderMapper orderMapper,
                        OrderProducer orderProducer) {
        this.repository = orderRepository;
        this.userClient = userClient;
        this.mapper = orderMapper;
        this.orderProducer = orderProducer;
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
    @Transactional
    public OrderResponseDto createOrder(OrderRequestDto orderDto) {
        UserResponseDto userResponseDto;
        try {
            userResponseDto = userClient.getUserById(orderDto.getUserId());
        } catch (FeignException.NotFound e) {
            throw new EntityNotFoundException("user ", orderDto.getUserId());
        } catch (FeignException.Unauthorized e) {
            throw new AccessDeniedException("Not enough rights");
        }

        Order order = mapper.toOrder(orderDto);
        Order savedOrder = repository.save(order);

        OrderEventDto event = new OrderEventDto();
        event.setOrderId(savedOrder.getId());
        event.setUserId(savedOrder.getUserId());
        event.setStatus(savedOrder.getOrderStatus());
        event.setCreationDate(savedOrder.getCreationDate());
        event.setSource("order-service");

        orderProducer.sendCreateOrderEvent(event);

        return getOrderResponseWithUser(savedOrder, userResponseDto);
    }

    /**
     * Finds an order by id.
     * @param id order's unique identifier
     * @return order as DTO with user info if found, empty if not found
     */
    public OrderResponseDto getOrderById(Long id) {
        Order order = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order", id));
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
            throw new EmptyEntityListException("orders", ids);
        }
        return orders;
    }

    /**
     * Finds orders with some status.
     * @param orderStatus orders' status
     * @return list of orders as DTOs
     */
    public List<OrderResponseDto> getOrdersByStatus(OrderStatus orderStatus) {
        List<OrderResponseDto> orders = repository.findByOrderStatus(orderStatus)
                .stream()
                .map(order -> getOrderResponseWithUser(order, userClient.getUserById(order.getUserId())))
                .toList();
        if (orders.isEmpty()) {
            throw new OrdersWithStatusNotFoundException(orderStatus.name());
        }
        return orders;
    }

    /**
     * Finds orders with some user ID.
     * @param userId User ID.
     * @return list of orders as DTOs
     */
    public List<OrderResponseDto> getOrdersByUserId(Long userId) {
        List<OrderResponseDto> orders = repository.findByUserId(userId)
                .stream()
                .map(order -> getOrderResponseWithUser(order, userClient.getUserById(order.getUserId())))
                .toList();
        if (orders.isEmpty()) {
            throw new OrdersWithUserIdNotFoundException(userId);
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
        int updated = repository.updateOrder(id, orderDto.getUserId(), orderDto.getOrderStatus().name(),
                orderDto.getCreationDate());
        if (updated == 0) {
            throw new EntityNotFoundException("Order", id);
        }
        Order order = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order", id));
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
            throw new EntityNotFoundException("Order", id);
        }
    }

    private OrderResponseDto getOrderResponseWithUser(Order order, UserResponseDto user) {
        OrderResponseDto orderResponseDto = mapper.toOrderResponseDto(order);
        orderResponseDto.setUser(user);
        return orderResponseDto;
    }

    public String getOrderOwnerEmail(Long orderId) {
        return repository.findById(orderId)
                .map(order -> userClient.getUserById(order.getUserId()).getEmail())
                .orElseThrow(() -> new EntityNotFoundException("order", orderId));
    }

    @KafkaListener(topics = "create_payment_v2", groupId = "order-service-group-v2")
    public void handleCreatePayment(PaymentEventDto paymentEventDto) {
        if (!"payment-service".equals(paymentEventDto.getSource())) {
            System.out.println("Ignoring event from non-payment source: " + paymentEventDto.getSource());
            return;
        }

        try {
            OrderRequestDto orderRequest = new OrderRequestDto();
            orderRequest.setUserId(paymentEventDto.getUserId());
            orderRequest.setOrderStatus(
                    paymentEventDto.getStatus() == PaymentStatus.COMPLETED
                            ? OrderStatus.CONFIRMED
                            : OrderStatus.CANCELED
            );
            orderRequest.setCreationDate(paymentEventDto.getCreationDate());
            updateOrderStatus(
                    paymentEventDto.getUserId(),
                    paymentEventDto.getStatus() == PaymentStatus.COMPLETED
                            ? OrderStatus.CONFIRMED
                            : OrderStatus.CANCELED
            );
        } catch (Exception e) {
            System.err.println("Error processing payment event: " + e.getMessage());
        }
    }

    @Transactional
    public void updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = repository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("order", orderId));

        order.setOrderStatus(newStatus);
        repository.save(order);
    }
}
