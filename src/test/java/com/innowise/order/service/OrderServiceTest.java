package com.innowise.order.service;

import com.innowise.order.client.UserClient;
import com.innowise.order.client.UserResponseDto;
import com.innowise.order.dto.OrderRequestDto;
import com.innowise.order.dto.OrderResponseDto;
import com.innowise.order.entity.Order;
import com.innowise.order.exception.EmptyEntityListException;
import com.innowise.order.exception.EntityNotFoundException;
import com.innowise.order.exception.OrdersWithStatusNotFoundException;
import com.innowise.order.mapper.OrderMapper;
import com.innowise.order.repository.OrderRepository;
import com.innowise.order.status.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.EmptyResultDataAccessException;

import java.time.LocalDate;
import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OrderServiceTest {

    @Mock
    private OrderMapper mapper;

    @Mock
    private OrderRepository repository;

    @Mock
    private UserClient userClient;

    @InjectMocks
    private OrderService service;

    private Order order;
    private Order order2;
    private UserResponseDto userResponseDto;
    private OrderRequestDto orderRequestDto;
    private OrderResponseDto orderResponseDto;
    private OrderResponseDto orderResponseDto2;

    @BeforeEach
    void setUpOrders() {
        MockitoAnnotations.openMocks(this);

        order = new Order();
        order.setId(1L);
        order.setUserId(1L);
        order.setStatus(Status.SHIPPED);
        order.setCreationDate(LocalDate.of(2025, 1, 1));

        userResponseDto = new UserResponseDto();
        userResponseDto.setId(1L);
        userResponseDto.setName("Hanna");
        userResponseDto.setSurname("Montana");
        userResponseDto.setBirthDate(LocalDate.of(2000, 3, 20));
        userResponseDto.setEmail("hanna00@gmail.com");

        orderRequestDto = new OrderRequestDto();
        orderRequestDto.setUserId(1L);
        orderRequestDto.setStatus(Status.SHIPPED);
        orderRequestDto.setCreationDate(LocalDate.of(2025, 1, 1));

        orderResponseDto = new OrderResponseDto();
        orderResponseDto.setId(1L);
        orderResponseDto.setUser(userResponseDto);
        orderResponseDto.setStatus(Status.SHIPPED);
        orderResponseDto.setCreationDate(LocalDate.of(2025, 1, 1));

        order2 = new Order();
        order2.setId(2L);
        order2.setUserId(1L);
        order2.setStatus(Status.COMPLETED);
        order2.setCreationDate(LocalDate.of(2025, 3, 10));

        orderResponseDto2 = new OrderResponseDto();
        orderResponseDto2.setId(2L);
        orderResponseDto2.setUser(userResponseDto);
        orderResponseDto2.setStatus(Status.COMPLETED);
        orderResponseDto2.setCreationDate(LocalDate.of(2025, 3, 10));
    }

    @Test
    void testCreateOrder() {
        when(mapper.toOrder(orderRequestDto)).thenReturn(order);
        when(repository.save(order)).thenReturn(order);
        when(mapper.toOrderResponseDto(order)).thenReturn(orderResponseDto);

        OrderResponseDto resultOrderResponseDto = service.createOrder(orderRequestDto);

        assertEquals(orderResponseDto, resultOrderResponseDto);
        verify(mapper).toOrder(orderRequestDto);
        verify(repository).save(order);
        verify(mapper).toOrderResponseDto(order);
    }

    @Test
    void testGetOrderById() {
        when(repository.findById(1L)).thenReturn(Optional.of(order));
        when(userClient.getUserById(1L)).thenReturn(userResponseDto);
        when(mapper.toOrderResponseDto(order)).thenReturn(orderResponseDto);

        OrderResponseDto resultOrderResponseDto = service.getOrderById((1L));

        assertNotNull(resultOrderResponseDto);
        assertEquals(orderResponseDto, resultOrderResponseDto);

        verify(repository).findById(1L);
        verify(userClient).getUserById(1L);
        verify(mapper).toOrderResponseDto(order);
    }

    @Test
    void testGetOrderByIdNotFound() {
        when(repository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.getOrderById(2L));

        verify(repository).findById(2L);
        verifyNoMoreInteractions(userClient, mapper);
    }

    @Test
    void testGetOrdersByIds() {
        List<Order> orders = List.of(order, order2);
        List<Long> ids = List.of(1L, 2L);

        when(repository.findAllById(ids)).thenReturn(orders);
        when(userClient.getUserById(1L)).thenReturn(userResponseDto);
        when(mapper.toOrderResponseDto(order)).thenReturn(orderResponseDto);
        when(mapper.toOrderResponseDto(order2)).thenReturn(orderResponseDto2);

        List<OrderResponseDto> resultList = service.getOrdersByIds(ids);

        assertEquals(2, resultList.size());
        assertTrue(resultList.contains(orderResponseDto));
        assertTrue(resultList.contains(orderResponseDto2));

        verify(repository).findAllById(ids);
        verify(userClient, times(2)).getUserById(1L);
        verify(mapper).toOrderResponseDto(order);
        verify(mapper).toOrderResponseDto(order2);
    }

    @Test
    void testGetOrdersByIdsNotFound() {
        List<Long> ids = List.of(1L, 2L);

        when(repository.findAllById(ids)).thenReturn(List.of());

        assertThrows(EmptyEntityListException.class, () -> service.getOrdersByIds(ids));

        verify(repository).findAllById(ids);
        verifyNoInteractions(userClient, mapper);
    }

    @Test
    void testGerOrdersByStatus() {
        order2.setStatus(Status.SHIPPED);

        List<Order> orders = List.of(order, order2);

        when(repository.findByStatus(Status.SHIPPED)).thenReturn(orders);
        when(userClient.getUserById(1L)).thenReturn(userResponseDto);
        when(mapper.toOrderResponseDto(order)).thenReturn(orderResponseDto);
        when(mapper.toOrderResponseDto(order2)).thenReturn(orderResponseDto2);

        List<OrderResponseDto> resultList = service.getOrdersByStatus(Status.SHIPPED);

        assertEquals(2, resultList.size());
        assertTrue(resultList.contains(orderResponseDto));
        assertTrue(resultList.contains(orderResponseDto2));

        verify(repository).findByStatus(Status.SHIPPED);
        verify(userClient, times(2)).getUserById(1L);
        verify(mapper).toOrderResponseDto(order);
        verify(mapper).toOrderResponseDto(order2);
    }

    @Test
    void testGetOrdersByStatusEmpty() {
        when(repository.findByStatus(Status.PENDS_PAY)).thenReturn(List.of());

        assertThrows(OrdersWithStatusNotFoundException.class, () -> service.getOrdersByStatus(Status.PENDS_PAY));

        verify(repository).findByStatus(Status.PENDS_PAY);
        verifyNoInteractions(userClient);
        verifyNoInteractions(mapper);
    }

    @Test
    void testUpdateOrderById() {
        Long orderId = 1L;
        OrderRequestDto updateOrderRequestDto = new OrderRequestDto();
        updateOrderRequestDto.setUserId(5L);
        updateOrderRequestDto.setStatus(Status.SHIPPED);
        updateOrderRequestDto.setCreationDate(LocalDate.of(2025, 3, 10));

        when(repository.updateOrder(orderId, 5L, Status.SHIPPED.name(),
                LocalDate.of(2025, 3, 10))).thenReturn(1);

        Order updatedOrder = new Order();
        updatedOrder.setId(orderId);
        updatedOrder.setUserId(5L);
        updatedOrder.setStatus(Status.SHIPPED);
        updatedOrder.setCreationDate(LocalDate.of(2025, 3, 10));

        OrderResponseDto updatedOrderResponse = new OrderResponseDto();
        updatedOrderResponse.setId(orderId);
        updatedOrderResponse.setUserId(5L);
        updatedOrderResponse.setStatus(Status.SHIPPED);
        updatedOrderResponse.setCreationDate(LocalDate.of(2025, 3, 10));

        when(repository.findById(orderId)).thenReturn(Optional.of(updatedOrder));
        when(userClient.getUserById(5L)).thenReturn(userResponseDto);
        when(mapper.toOrderResponseDto(updatedOrder)).thenReturn(updatedOrderResponse);

        OrderResponseDto resultOrderResponseDto = service.updateOrderById(orderId, updateOrderRequestDto);

        assertNotNull(resultOrderResponseDto);
        assertEquals(updatedOrderResponse, resultOrderResponseDto);

        verify(repository).updateOrder(orderId, 5L, Status.SHIPPED.name(),
                LocalDate.of(2025, 3, 10));
        verify(repository).findById(orderId);
        verify(userClient).getUserById(5L);
        verify(mapper).toOrderResponseDto(updatedOrder);
    }

    @Test
    void testUpdateOrderByIdNotUpdated() {
        Long orderId = 599L;
        OrderRequestDto updateOrderRequestDto = new OrderRequestDto();
        updateOrderRequestDto.setUserId(5L);
        updateOrderRequestDto.setStatus(Status.SHIPPED);
        updateOrderRequestDto.setCreationDate(LocalDate.of(2025, 3, 10));

        when(repository.updateOrder(anyLong(), anyLong(), anyString(), any())).thenReturn(0);

        assertThrows(EntityNotFoundException.class, () -> service.updateOrderById(orderId, updateOrderRequestDto));

        verify(repository).updateOrder(orderId, 5L, Status.SHIPPED.name(),
                LocalDate.of(2025, 3, 10));
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(userClient);
        verifyNoInteractions(mapper);
    }

    @Test
    void testUpdateOrderByIdNotFoundAfterUpdate() {
        Long orderId = 1L;
        OrderRequestDto updateOrderRequestDto = new OrderRequestDto();
        updateOrderRequestDto.setUserId(5L);
        updateOrderRequestDto.setStatus(Status.SHIPPED);
        updateOrderRequestDto.setCreationDate(LocalDate.of(2025, 3, 10));

        when(repository.updateOrder(anyLong(), anyLong(), anyString(), any())).thenReturn(1);

        when(repository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.updateOrderById(orderId, updateOrderRequestDto));
        verify(repository).updateOrder(orderId, 5L, Status.SHIPPED.name(),
                LocalDate.of(2025, 3, 10));
        verify(repository).findById(orderId);
        verifyNoInteractions(userClient);
        verifyNoInteractions(mapper);
    }

    @Test
    void testDeleteOrderById() {
        Long orderId = 5L;
        doNothing().when(repository).deleteById(orderId);

        assertDoesNotThrow(() -> service.deleteOrderById(orderId));

        verify(repository).deleteById(orderId);
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(userClient);
        verifyNoInteractions(mapper);
    }

    @Test
    void testDeleteOrderByIdNotFound() {
        Long orderId = 5L;

        doThrow(new EmptyResultDataAccessException(1)).when(repository).deleteById(orderId);

        assertThrows(EntityNotFoundException.class, () -> service.deleteOrderById(orderId));

        verify(repository).deleteById(orderId);
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(userClient);
        verifyNoInteractions(mapper);
    }

    @Test
    void testGetOrdersByEmail() {
        String email = "hanna00@gmail.com";
        List<Order> orders = List.of(order);

        when(userClient.getUserByEmail(email)).thenReturn(userResponseDto);
        when(repository.findByUserId(1L)).thenReturn(orders);
        when(mapper.toOrderResponseDto(order)).thenReturn(orderResponseDto);

        List<OrderResponseDto> resultOrders = service.getOrdersByEmail(email);

        assertEquals(1, resultOrders.size());
        assertEquals(1L, resultOrders.get(0).getId());
        assertEquals(email, resultOrders.get(0).getUser().getEmail());

        verify(userClient).getUserByEmail(email);
        verify(repository).findByUserId(1L);
        verify(mapper).toOrderResponseDto(order);
    }

    @Test
    void testGetUserByEmail() {
        String email = "hanna00@gmail.com";

        when(userClient.getUserByEmail(email)).thenReturn(userResponseDto);

        UserResponseDto resultUserResponseDto = service.getUserByEmail(email);
        assertEquals(userResponseDto, resultUserResponseDto);
        assertEquals(1L, resultUserResponseDto.getId());
        assertEquals(email, resultUserResponseDto.getEmail());

        verify(userClient).getUserByEmail(email);
        verifyNoInteractions(repository);
        verifyNoInteractions(mapper);
    }
}
