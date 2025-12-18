package com.innowise.order.service;

import com.innowise.order.dto.OrderItemRequestDto;
import com.innowise.order.dto.OrderItemResponseDto;
import com.innowise.order.entity.Item;
import com.innowise.order.entity.Order;
import com.innowise.order.entity.OrderItem;
import com.innowise.order.exception.EmptyEntityListException;
import com.innowise.order.exception.EmptyOrderItemListSingleIdException;
import com.innowise.order.exception.EntityNotFoundException;
import com.innowise.order.mapper.OrderItemMapper;
import com.innowise.order.repository.OrderItemRepository;
import com.innowise.order.status.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.EmptyResultDataAccessException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OrderItemServiceTest {

    @Mock
    private OrderItemMapper mapper;

    @Mock
    private OrderItemRepository repository;

    @InjectMocks
    private OrderItemService service;

    private Order order;
    private Item item;
    private Item item2;
    private OrderItem orderItem;
    private OrderItem orderItem2;
    private OrderItemRequestDto orderItemRequestDto;
    private OrderItemResponseDto orderItemResponseDto;
    private OrderItemResponseDto orderItemResponseDto2;

    @BeforeEach
    void setUpOrderItems() {
        MockitoAnnotations.openMocks(this);

        order = new Order();
        order.setId(1L);
        order.setUserId(1L);
        order.setOrderStatus(OrderStatus.SHIPPED);
        order.setCreationDate(LocalDate.of(2025, 1, 1));

        item = new Item();
        item.setId(1L);
        item.setName("Jeans");
        item.setPrice(BigDecimal.valueOf(38.9d));

        item2 = new Item();
        item2.setId(2L);
        item2.setName("Toothpaste");
        item2.setPrice(BigDecimal.valueOf(4.85d));

        orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setOrder(order);
        orderItem.setItem(item);
        orderItem.setQuantity(2);

        orderItem2 = new OrderItem();
        orderItem2.setId(2L);
        orderItem2.setOrder(order);
        orderItem2.setItem(item2);
        orderItem2.setQuantity(1);

        orderItemRequestDto = new OrderItemRequestDto();
        orderItemRequestDto.setOrderId(1L);
        orderItemRequestDto.setItemId(1L);
        orderItemRequestDto.setQuantity(2);

        orderItemResponseDto = new OrderItemResponseDto();
        orderItemResponseDto.setId(1L);
        orderItemResponseDto.setOrderId(1L);
        orderItemResponseDto.setItemId(1L);
        orderItemResponseDto.setQuantity(2);

        orderItemResponseDto2 = new OrderItemResponseDto();
        orderItemResponseDto2.setId(2L);
        orderItemResponseDto2.setOrderId(1L);
        orderItemResponseDto2.setItemId(2L);
        orderItemResponseDto2.setQuantity(1);
    }

    @Test
    void testCreateOrderItem() {
        when(mapper.toOrderItem(orderItemRequestDto)).thenReturn(orderItem);
        when(repository.save(orderItem)).thenReturn(orderItem);
        when(mapper.toOrderItemResponseDto(orderItem)).thenReturn(orderItemResponseDto);

        OrderItemResponseDto resultOrderItem = service.createOrderItem(orderItemRequestDto);

        assertNotNull(resultOrderItem);
        assertEquals(orderItemResponseDto.getId(), resultOrderItem.getId());
        assertEquals(orderItemResponseDto.getOrderId(), resultOrderItem.getItemId());
        assertEquals(orderItemResponseDto.getItemId(), resultOrderItem.getItemId());
        assertEquals(orderItemResponseDto.getQuantity(), resultOrderItem.getQuantity());

        verify(mapper).toOrderItem(orderItemRequestDto);
        verify(repository).save(orderItem);
        verify(mapper).toOrderItemResponseDto(orderItem);
    }

    @Test
    void testGetOrderItemById() {
        Long orderItemId = 1L;

        when(repository.findById(orderItemId)).thenReturn(Optional.of(orderItem));
        when(mapper.toOrderItemResponseDto(orderItem)).thenReturn(orderItemResponseDto);

        OrderItemResponseDto resultOrderItemResponse = service.getOrderItemById(orderItemId);

        assertNotNull(resultOrderItemResponse);
        assertEquals(orderItemResponseDto.getId(), resultOrderItemResponse.getId());
        assertEquals(orderItemResponseDto.getOrderId(), resultOrderItemResponse.getOrderId());
        assertEquals(orderItemResponseDto.getItemId(), resultOrderItemResponse.getItemId());
        assertEquals(orderItemResponseDto.getQuantity(), resultOrderItemResponse.getQuantity());

        verify(repository).findById(orderItemId);
        verify(mapper).toOrderItemResponseDto(orderItem);
    }

    @Test
    void testGetOrderItemByIdNotFound() {
        Long orderItemId = 58L;

        when(repository.findById(orderItemId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.getOrderItemById(orderItemId));

        verify(repository).findById(orderItemId);
        verifyNoInteractions(mapper);
    }

    @Test
    void testGetOrderItemsByIds() {
        List<Long> ids = List.of(1L, 2L);
        List<OrderItem> orderItems = List.of(orderItem, orderItem2);

        when(repository.findAllById(ids)).thenReturn(orderItems);
        when(mapper.toOrderItemResponseDto(orderItem)).thenReturn(orderItemResponseDto);
        when(mapper.toOrderItemResponseDto(orderItem2)).thenReturn(orderItemResponseDto2);

        List<OrderItemResponseDto> resultOrderItemList = service.getOrderItemsByIds(ids);

        assertNotNull(resultOrderItemList);
        assertEquals(2, resultOrderItemList.size());
        assertTrue(resultOrderItemList.contains(orderItemResponseDto));
        assertTrue(resultOrderItemList.contains(orderItemResponseDto2));

        verify(repository).findAllById(ids);
        verify(mapper).toOrderItemResponseDto(orderItem);
        verify(mapper).toOrderItemResponseDto(orderItem2);
    }

    @Test
    void testGetOrderItemsByIdsEmptyList() {
        List<Long> ids = List.of(100L, 200L);

        when(repository.findAllById(ids)).thenReturn(List.of());

        assertThrows(EmptyEntityListException.class, () -> service.getOrderItemsByIds(ids));

        verify(repository).findAllById(ids);
        verifyNoInteractions(mapper);
    }

    @Test
    void testGetOrderItemsByOrderId() {
        Long orderId = 1L;
        List<OrderItem> orderItems = List.of(orderItem, orderItem2);

        when(repository.findByOrderId(orderId)).thenReturn(orderItems);
        when(mapper.toOrderItemResponseDto(orderItem)).thenReturn(orderItemResponseDto);
        when(mapper.toOrderItemResponseDto(orderItem2)).thenReturn(orderItemResponseDto2);

        List<OrderItemResponseDto> resultOrderItemsList = service.getOrderItemsByOrderId(orderId);

        assertNotNull(resultOrderItemsList);
        assertEquals(2, resultOrderItemsList.size());
        assertTrue(resultOrderItemsList.contains(orderItemResponseDto));
        assertTrue(resultOrderItemsList.contains(orderItemResponseDto2));

        verify(repository).findByOrderId(orderId);
        verify(mapper).toOrderItemResponseDto(orderItem);
        verify(mapper).toOrderItemResponseDto(orderItem2);
    }

    @Test
    void testGetOrderItemsByOrderIdEmptyList() {
        Long orderId = 5L;

        when(repository.findByOrderId(orderId)).thenReturn(List.of());

        assertThrows(EmptyOrderItemListSingleIdException.class, () -> service.getOrderItemsByOrderId(orderId));

        verify(repository).findByOrderId(orderId);
        verifyNoInteractions(mapper);
    }

    @Test
    void testGetOrderItemsByItemId() {
        Long itemId = 1L;
        List<OrderItem> orderItems = List.of(orderItem, orderItem2);

        when(repository.findByItemId(itemId)).thenReturn(orderItems);
        when(mapper.toOrderItemResponseDto(orderItem)).thenReturn(orderItemResponseDto);
        when(mapper.toOrderItemResponseDto(orderItem2)).thenReturn(orderItemResponseDto2);

        List<OrderItemResponseDto> resultOrderItemsList = service.getOrderItemsByItemId(itemId);

        assertNotNull(resultOrderItemsList);
        assertEquals(2, resultOrderItemsList.size());
        assertTrue(resultOrderItemsList.contains(orderItemResponseDto));
        assertTrue(resultOrderItemsList.contains(orderItemResponseDto2));

        verify(repository).findByItemId(itemId);
        verify(mapper).toOrderItemResponseDto(orderItem);
        verify(mapper).toOrderItemResponseDto(orderItem2);
    }

    @Test
    void testGetOrderItemsByItemIdEmptyList() {
        Long itemId = 5L;

        when(repository.findByItemId(itemId)).thenReturn(List.of());

        assertThrows(EmptyOrderItemListSingleIdException.class, () -> service.getOrderItemsByItemId(itemId));

        verify(repository).findByItemId(itemId);
        verifyNoInteractions(mapper);
    }

    @Test
    void testUpdateOrderItemById() {
        Long orderItemId = 1L;

        OrderItemRequestDto updateOrderItem = new OrderItemRequestDto();
        updateOrderItem.setOrderId(1L);
        updateOrderItem.setItemId(2L);
        updateOrderItem.setQuantity(3);

        OrderItem updatedOrderItem = new OrderItem();
        updatedOrderItem.setId(orderItemId);
        updatedOrderItem.setOrder(order);
        updatedOrderItem.setItem(item2);
        updatedOrderItem.setQuantity(3);

        OrderItemResponseDto updatedOrderItemResponse = new OrderItemResponseDto();
        updatedOrderItemResponse.setId(orderItemId);
        updatedOrderItemResponse.setOrderId(1L);
        updatedOrderItemResponse.setItemId(2L);
        updatedOrderItemResponse.setQuantity(3);

        when(repository.updateOrderItem(orderItemId, 1L, 2L, 3)).thenReturn(1);
        when(repository.findById(orderItemId)).thenReturn(Optional.of(updatedOrderItem));
        when(mapper.toOrderItemResponseDto(updatedOrderItem)).thenReturn(updatedOrderItemResponse);

        OrderItemResponseDto resultOrderItem = service.updateOrderItemById(orderItemId, updateOrderItem);

        assertNotNull(resultOrderItem);
        assertEquals(updatedOrderItemResponse, resultOrderItem);

        verify(repository).updateOrderItem(orderItemId, 1L, 2L, 3);
        verify(repository).findById(orderItemId);
        verify(mapper).toOrderItemResponseDto(updatedOrderItem);
    }

    @Test
    void testUpdateOrderItemByIdNotUpdated() {
        Long orderItemId = 5L;

        OrderItemRequestDto updateOrderItem = new OrderItemRequestDto();
        updateOrderItem.setOrderId(1L);
        updateOrderItem.setItemId(2L);
        updateOrderItem.setQuantity(3);

        when(repository.updateOrderItem(orderItemId, 1L, 2L, 3)).thenReturn(0);

        assertThrows(EntityNotFoundException.class, () -> service.updateOrderItemById(orderItemId, updateOrderItem));

        verify(repository).updateOrderItem(orderItemId, 1L, 2L, 3);
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(mapper);
    }

    @Test
    void testUpdateOrderItemNotFoundAfterUpdate() {
        Long orderItemId = 1L;

        OrderItemRequestDto updateOrderItem = new OrderItemRequestDto();
        updateOrderItem.setOrderId(1L);
        updateOrderItem.setItemId(2L);
        updateOrderItem.setQuantity(3);

        when(repository.updateOrderItem(orderItemId, 1L, 2L, 3)).thenReturn(1);
        when(repository.findById(orderItemId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.updateOrderItemById(orderItemId, updateOrderItem));

        verify(repository).updateOrderItem(orderItemId, 1L, 2L, 3);
        verify(repository).findById(orderItemId);
        verifyNoInteractions(mapper);
    }

    @Test
    void testDeleteOrderItemById() {
        Long orderItemId = 1L;

        doNothing().when(repository).deleteById(orderItemId);

        assertDoesNotThrow(() -> service.deleteOrderItemById(orderItemId));

        verify(repository).deleteById(orderItemId);
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(mapper);
    }

    @Test
    void testDeleteOrderItemByIdNotFound() {
        Long orderItemId = 58L;

        doThrow(new EmptyResultDataAccessException(1)).when(repository).deleteById(orderItemId);
        assertThrows(EntityNotFoundException.class, () -> service.deleteOrderItemById(orderItemId));

        verify(repository).deleteById(orderItemId);
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(mapper);
    }
}
