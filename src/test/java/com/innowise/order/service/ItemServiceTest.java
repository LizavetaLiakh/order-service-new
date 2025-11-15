package com.innowise.order.service;

import com.innowise.order.dto.ItemRequestDto;
import com.innowise.order.dto.ItemResponseDto;
import com.innowise.order.entity.Item;
import com.innowise.order.exception.EmptyEntityListException;
import com.innowise.order.exception.EntityNotFoundException;
import com.innowise.order.mapper.ItemMapper;
import com.innowise.order.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.EmptyResultDataAccessException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ItemServiceTest {

    @Mock
    private ItemMapper mapper;

    @Mock
    private ItemRepository repository;

    @InjectMocks
    private ItemService service;

    private Item item;
    private Item item2;
    private ItemRequestDto itemRequestDto;
    private ItemResponseDto itemResponseDto;
    private ItemResponseDto itemResponseDto2;

    private final static String UPDATE_NAME = "Blue jeans";
    private final static BigDecimal UPDATE_PRICE = BigDecimal.valueOf(70.5d);

    @BeforeEach
    void setUpItems() {
        MockitoAnnotations.openMocks(this);

        item = new Item();
        item.setId(1L);
        item.setName("Jeans");
        item.setPrice(BigDecimal.valueOf(38.9d));

        item2 = new Item();
        item2.setId(2L);
        item2.setName("Toothpaste");
        item2.setPrice(BigDecimal.valueOf(4.85d));

        itemRequestDto = new ItemRequestDto();
        itemRequestDto.setName("Jeans");
        itemRequestDto.setPrice(BigDecimal.valueOf(38.9d));

        itemResponseDto = new ItemResponseDto();
        itemResponseDto.setId(1L);
        itemResponseDto.setName("Jeans");
        itemResponseDto.setPrice(BigDecimal.valueOf(38.9d));

        itemResponseDto2 = new ItemResponseDto();
        itemResponseDto2.setId(2L);
        itemResponseDto2.setName("Toothpaste");
        itemResponseDto2.setPrice(BigDecimal.valueOf(4.85d));
    }

    @Test
    void testCrateItem() {
        when(mapper.toItem(itemRequestDto)).thenReturn(item);
        when(repository.save(item)).thenReturn(item);
        when(mapper.toItemResponseDto(item)).thenReturn(itemResponseDto);

        ItemResponseDto resultItem = service.createItem(itemRequestDto);

        assertEquals(itemResponseDto, resultItem);
        verify(mapper).toItem(itemRequestDto);
        verify(repository).save(item);
        verify(mapper).toItemResponseDto(item);
    }

    @Test
    void testGetItemById() {
        when(repository.findById(1L)).thenReturn(Optional.of(item));
        when(mapper.toItemResponseDto(item)).thenReturn(itemResponseDto);

        ItemResponseDto resultItem = service.getItemById((1L));

        assertNotNull(resultItem);
        assertEquals(itemResponseDto, resultItem);

        verify(repository).findById(1L);
        verify(mapper).toItemResponseDto(item);
    }

    @Test
    void testGetItemByIdNotFound() {
        when(repository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.getItemById(2L));

        verify(repository).findById(2L);
        verifyNoMoreInteractions(mapper);
    }

    @Test
    void testGetItemsByIds() {
        List<Item> orders = List.of(item, item2);
        List<Long> ids = List.of(1L, 2L);

        when(repository.findAllById(ids)).thenReturn(orders);
        when(mapper.toItemResponseDto(item)).thenReturn(itemResponseDto);
        when(mapper.toItemResponseDto(item2)).thenReturn(itemResponseDto2);

        List<ItemResponseDto> resultList = service.getItemsByIds(ids);

        assertEquals(2, resultList.size());
        assertTrue(resultList.contains(itemResponseDto));
        assertTrue(resultList.contains(itemResponseDto2));

        verify(repository).findAllById(ids);
        verify(mapper).toItemResponseDto(item);
        verify(mapper).toItemResponseDto(item2);
    }

    @Test
    void testGetItemsByIdsNotFound() {
        List<Long> ids = List.of(1L, 2L);

        when(repository.findAllById(ids)).thenReturn(List.of());

        assertThrows(EmptyEntityListException.class, () -> service.getItemsByIds(ids));

        verify(repository).findAllById(ids);
        verifyNoInteractions(mapper);
    }

    @Test
    void testUpdateItemById() {
        Long itemId = 1L;

        ItemRequestDto updateItem = new ItemRequestDto();
        updateItem.setName(UPDATE_NAME);
        updateItem.setPrice(UPDATE_PRICE);

        when(repository.updateItem(itemId, updateItem.getName(), updateItem.getPrice())).thenReturn(1);

        Item updatedItem = new Item();
        updatedItem.setId(itemId);
        updatedItem.setName(UPDATE_NAME);
        updateItem.setPrice(UPDATE_PRICE);

        ItemResponseDto updatedItemResponse = new ItemResponseDto();
        updatedItemResponse.setId(itemId);
        updatedItemResponse.setName(UPDATE_NAME);
        updatedItemResponse.setPrice(UPDATE_PRICE);

        when(repository.findById(itemId)).thenReturn(Optional.of(updatedItem));
        when(mapper.toItemResponseDto(updatedItem)).thenReturn(updatedItemResponse);

        ItemResponseDto resultItemResponse = service.updateItemById(itemId, updateItem);

        assertNotNull(resultItemResponse);
        assertEquals(updatedItemResponse, resultItemResponse);

        verify(repository).updateItem(itemId, UPDATE_NAME, UPDATE_PRICE);
        verify(repository).findById(itemId);
        verify(mapper).toItemResponseDto(updatedItem);
    }

    @Test
    void testUpdateItemByIdNotFound() {
        Long itemId = 599L;

        ItemRequestDto updateItemRequest = new ItemRequestDto();
        updateItemRequest.setName(UPDATE_NAME);
        updateItemRequest.setPrice(UPDATE_PRICE);

        when(repository.updateItem(anyLong(), anyString(), any())).thenReturn(0);

        assertThrows(EntityNotFoundException.class, () -> service.updateItemById(itemId, updateItemRequest));

        verify(repository).updateItem(itemId, UPDATE_NAME, UPDATE_PRICE);
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(mapper);
    }

    @Test
    void testUpdateItemBuIdNotFoundAfterUpdate() {
        Long itemId = 1L;

        ItemRequestDto updateItemRequest = new ItemRequestDto();
        updateItemRequest.setName(UPDATE_NAME);
        updateItemRequest.setPrice(UPDATE_PRICE);

        when(repository.updateItem(anyLong(), anyString(), any())).thenReturn(1);

        when(repository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.updateItemById(itemId, updateItemRequest));
        verify(repository).updateItem(itemId, UPDATE_NAME, UPDATE_PRICE);
        verify(repository).findById(itemId);
        verifyNoInteractions(mapper);
    }

    @Test
    void testDeleteItemById() {
        Long itemId = 5L;
        doNothing().when(repository).deleteById(itemId);

        assertDoesNotThrow(() -> service.deleteItemById(itemId));

        verify(repository).deleteById(itemId);
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(mapper);
    }

    @Test
    void testDeleteItemByIdNotFound() {
        Long itemId = 5L;

        doThrow(new EmptyResultDataAccessException(1)).when(repository).deleteById(itemId);

        assertThrows(EntityNotFoundException.class, () -> service.deleteItemById(itemId));

        verify(repository).deleteById(itemId);
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(mapper);
    }
}
