package com.innowise.order.integration;

import com.innowise.order.dto.OrderItemRequestDto;
import com.innowise.order.dto.OrderItemResponseDto;
import com.innowise.order.entity.Item;
import com.innowise.order.entity.Order;
import com.innowise.order.exception.EmptyEntityListException;
import com.innowise.order.exception.EmptyOrderItemListSingleIdException;
import com.innowise.order.exception.EntityNotFoundException;
import com.innowise.order.repository.ItemRepository;
import com.innowise.order.repository.OrderItemRepository;
import com.innowise.order.repository.OrderRepository;
import com.innowise.order.service.OrderItemService;
import com.innowise.order.status.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OrderItemIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private OrderItemService service;

    @Autowired
    private OrderItemRepository repository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @BeforeEach
    void clean() {
        repository.deleteAll();
        itemRepository.deleteAll();
        orderRepository.deleteAll();
    }

    @Test
    void testCreateAndGetOrderItem() {
        Order order = new Order();
        order.setUserId(1L);
        order.setStatus(Status.SHIPPED);
        order.setCreationDate(LocalDate.of(2025, 1, 10));
        Order savedOrder = orderRepository.save(order);

        Item item = new Item();
        item.setName("Item name");
        item.setPrice(BigDecimal.valueOf(10.55));
        Item savedItem = itemRepository.save(item);

        OrderItemRequestDto orderItemRequestDto = new OrderItemRequestDto();
        orderItemRequestDto.setOrderId(savedOrder.getId());
        orderItemRequestDto.setItemId(savedItem.getId());
        orderItemRequestDto.setQuantity(2);

        OrderItemResponseDto createdOrderItemResponseDto = service.createOrderItem(orderItemRequestDto);

        assertNotNull(createdOrderItemResponseDto);
        assertEquals(savedOrder.getId(), createdOrderItemResponseDto.getOrderId());
        assertEquals(savedItem.getId(), createdOrderItemResponseDto.getItemId());
        assertEquals(2, createdOrderItemResponseDto.getQuantity());

        OrderItemResponseDto foundOrderItemResponseDto = service.getOrderItemById(createdOrderItemResponseDto.getId());
        assertEquals(createdOrderItemResponseDto.getId(), foundOrderItemResponseDto.getId());
        assertEquals(createdOrderItemResponseDto.getOrderId(), foundOrderItemResponseDto.getOrderId());
        assertEquals(createdOrderItemResponseDto.getItemId(), foundOrderItemResponseDto.getItemId());
        assertEquals(createdOrderItemResponseDto.getQuantity(), foundOrderItemResponseDto.getQuantity());
    }

    @Test
    void testGetOrderItemsByIds() {
        Order order = new Order();
        order.setUserId(1L);
        order.setStatus(Status.SHIPPED);
        order.setCreationDate(LocalDate.of(2025, 1, 10));
        Order savedOrder = orderRepository.save(order);

        Item item1 = new Item();
        item1.setName("Item name 1");
        item1.setPrice(BigDecimal.valueOf(10.55));
        Item savedItem1 = itemRepository.save(item1);

        Item item2 = new Item();
        item2.setName("Item name 2");
        item2.setPrice(BigDecimal.valueOf(15.00));
        Item savedItem2 = itemRepository.save(item2);

        OrderItemRequestDto orderItemRequestDto1 = new OrderItemRequestDto();
        orderItemRequestDto1.setOrderId(savedOrder.getId());
        orderItemRequestDto1.setItemId(savedItem1.getId());
        orderItemRequestDto1.setQuantity(2);

        OrderItemRequestDto orderItemRequestDto2 = new OrderItemRequestDto();
        orderItemRequestDto2.setOrderId(savedOrder.getId());
        orderItemRequestDto2.setItemId(savedItem2.getId());
        orderItemRequestDto2.setQuantity(1);

        OrderItemResponseDto orderItemResponseDto1 = service.createOrderItem(orderItemRequestDto1);
        OrderItemResponseDto orderItemResponseDto2 = service.createOrderItem(orderItemRequestDto2);

        List<OrderItemResponseDto> orderItems = service.getOrderItemsByIds(List.of(
                orderItemResponseDto1.getId(),
                orderItemResponseDto2.getId()
        ));
        assertEquals(2, orderItems.size());
    }

    @Test
    void testGetOrderItemsByOrderId() {
        Order order = new Order();
        order.setUserId(1L);
        order.setStatus(Status.SHIPPED);
        order.setCreationDate(LocalDate.of(2025, 1, 10));
        Order savedOrder = orderRepository.save(order);

        Item item = new Item();
        item.setName("Item name");
        item.setPrice(BigDecimal.valueOf(10.55));
        Item savedItem = itemRepository.save(item);

        OrderItemRequestDto orderItemRequestDto = new OrderItemRequestDto();
        orderItemRequestDto.setOrderId(savedOrder.getId());
        orderItemRequestDto.setItemId(savedItem.getId());
        orderItemRequestDto.setQuantity(1);

        service.createOrderItem(orderItemRequestDto);

        List<OrderItemResponseDto> orderItems = service.getOrderItemsByOrderId(savedOrder.getId());
        assertEquals(1, orderItems.size());
        assertEquals(savedOrder.getId(), orderItems.get(0).getOrderId());
    }

    @Test
    void testGetOrderItemsByItemId() {
        Order order = new Order();
        order.setUserId(1L);
        order.setStatus(Status.SHIPPED);
        order.setCreationDate(LocalDate.of(2025, 1, 10));
        Order savedOrder = orderRepository.save(order);

        Item item = new Item();
        item.setName("Item name");
        item.setPrice(BigDecimal.valueOf(10.55));
        Item savedItem = itemRepository.save(item);

        OrderItemRequestDto orderItemRequestDto = new OrderItemRequestDto();
        orderItemRequestDto.setOrderId(savedOrder.getId());
        orderItemRequestDto.setItemId(savedItem.getId());
        orderItemRequestDto.setQuantity(4);

        service.createOrderItem(orderItemRequestDto);

        List<OrderItemResponseDto> orderItems = service.getOrderItemsByItemId(savedItem.getId());
        assertEquals(1, orderItems.size());
        assertEquals(savedItem.getId(), orderItems.get(0).getItemId());
    }

    @Test
    void testUpdateOrderItemById() {
        Order order = new Order();
        order.setUserId(1L);
        order.setStatus(Status.SHIPPED);
        order.setCreationDate(LocalDate.of(2025, 1, 10));
        Order savedOrder = orderRepository.save(order);

        Item item = new Item();
        item.setName("Item name");
        item.setPrice(BigDecimal.valueOf(10.55));
        Item savedItem = itemRepository.save(item);

        OrderItemRequestDto orderItemRequestDto = new OrderItemRequestDto();
        orderItemRequestDto.setOrderId(savedOrder.getId());
        orderItemRequestDto.setItemId(savedItem.getId());
        orderItemRequestDto.setQuantity(3);

        OrderItemResponseDto createdOrderItemRequestDto = service.createOrderItem(orderItemRequestDto);

        OrderItemRequestDto updateOrderItemRequestDto= new OrderItemRequestDto();
        updateOrderItemRequestDto.setOrderId(savedOrder.getId());
        updateOrderItemRequestDto.setItemId(savedItem.getId());
        updateOrderItemRequestDto.setQuantity(10);

        OrderItemResponseDto updated = service.updateOrderItemById(createdOrderItemRequestDto.getId(),
                updateOrderItemRequestDto);

        assertEquals(10, updated.getQuantity());
        assertEquals(createdOrderItemRequestDto.getId(), updated.getId());
    }

    @Test
    void testUpdateOrderItemByIdNotFound() {
        Order order = new Order();
        order.setUserId(1L);
        order.setStatus(Status.SHIPPED);
        order.setCreationDate(LocalDate.of(2025, 1, 10));
        Order savedOrder = orderRepository.save(order);

        Item item = new Item();
        item.setName("Item name");
        item.setPrice(BigDecimal.valueOf(10.55));
        Item savedItem = itemRepository.save(item);

        OrderItemRequestDto updateRequest = new OrderItemRequestDto();
        updateRequest.setOrderId(savedOrder.getId());
        updateRequest.setItemId(savedItem.getId());
        updateRequest.setQuantity(5);

        Long nonExistentId = 59L;
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () ->
                service.updateOrderItemById(nonExistentId, updateRequest));

        assertEquals("Order item with id " + nonExistentId + " not found", ex.getMessage());
    }

    @Test
    void testDeleteOrderItemById() {
        Order order = new Order();
        order.setUserId(1L);
        order.setStatus(Status.SHIPPED);
        order.setCreationDate(LocalDate.of(2025, 1, 10));
        Order savedOrder = orderRepository.save(order);

        Item item = new Item();
        item.setName("Item name");
        item.setPrice(BigDecimal.valueOf(10.55));
        Item savedItem = itemRepository.save(item);

        OrderItemRequestDto orderItemRequestDto = new OrderItemRequestDto();
        orderItemRequestDto.setOrderId(savedOrder.getId());
        orderItemRequestDto.setItemId(savedItem.getId());
        orderItemRequestDto.setQuantity(3);

        OrderItemResponseDto createdOrderItemResponseDto = service.createOrderItem(orderItemRequestDto);

        service.deleteOrderItemById(createdOrderItemResponseDto.getId());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () ->
                service.getOrderItemById(createdOrderItemResponseDto.getId()));

        assertEquals("Order item with id " + createdOrderItemResponseDto.getId() + " not found",
                ex.getMessage());
    }

    @Test
    void testGetOrderItemByIdNotFound() {
        Long nonExistentId = 59L;
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () ->
                service.getOrderItemById(nonExistentId));
        assertEquals("Order item with id " + nonExistentId + " not found", ex.getMessage());
    }

    @Test
    void testGetOrderItemsByIdsEmpty() {
        EmptyEntityListException ex = assertThrows(EmptyEntityListException.class, () ->
                service.getOrderItemsByIds(List.of(1L, 2L)));
        assertTrue(ex.getMessage().contains("No order items found with ids: "));
    }

    @Test
    void testGetOrderItemsByOrderIdEmpty() {
        Long nonExistentOrderId = 999L;
        EmptyOrderItemListSingleIdException ex = assertThrows(EmptyOrderItemListSingleIdException.class, () ->
                service.getOrderItemsByOrderId(nonExistentOrderId));
        assertTrue(ex.getMessage().contains("No order items found with order id"));
    }

    @Test
    void testGetOrderItemsByItemIdEmpty() {
        Long nonExistentItemId = 999L;
        EmptyOrderItemListSingleIdException ex = assertThrows(EmptyOrderItemListSingleIdException.class, () ->
                service.getOrderItemsByItemId(nonExistentItemId));
        assertTrue(ex.getMessage().contains("No order items found with item id"));
    }
}
