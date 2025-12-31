package com.innowise.order.integration;

import com.innowise.order.dto.ItemRequestDto;
import com.innowise.order.dto.ItemResponseDto;
import com.innowise.order.exception.EntityNotFoundException;
import com.innowise.order.repository.ItemRepository;
import com.innowise.order.service.ItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class ItemServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private ItemService service;

    @Autowired
    private ItemRepository repository;

    @BeforeEach
    void clean() {
        repository.deleteAll();
    }

    @Test
    void testCreateAndGetItem() {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setName("Test Item");
        itemRequestDto.setPrice(BigDecimal.valueOf(159.89));

        ItemResponseDto createdItemResponseDto = service.createItem(itemRequestDto);

        assertNotNull(createdItemResponseDto.getId());
        assertEquals("Test Item", createdItemResponseDto.getName());
        assertEquals(BigDecimal.valueOf(159.89), createdItemResponseDto.getPrice());

        ItemResponseDto foundItem = service.getItemById(createdItemResponseDto.getId());

        assertEquals(createdItemResponseDto.getId(), foundItem.getId());
        assertEquals(createdItemResponseDto.getName(), foundItem.getName());
        assertEquals(createdItemResponseDto.getPrice(), foundItem.getPrice());
    }

    @Test
    void testUpdateItemById() {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setName("Start name");
        itemRequestDto.setPrice(BigDecimal.valueOf(159.89));

        ItemResponseDto createdItem = service.createItem(itemRequestDto);

        ItemRequestDto updateItemRequestDto = new ItemRequestDto();
        updateItemRequestDto.setName("Name after update");
        updateItemRequestDto.setPrice(BigDecimal.valueOf(120.57));

        ItemResponseDto updatedItem = service.updateItemById(createdItem.getId(), updateItemRequestDto);

        assertEquals(createdItem.getId(), updatedItem.getId());
        assertEquals("Name after update", updatedItem.getName());
        assertEquals(BigDecimal.valueOf(120.57), updatedItem.getPrice());
    }

    @Test
    void updateItemByIdNotFound() {
        ItemRequestDto updateItemRequestDto = new ItemRequestDto();
        updateItemRequestDto.setName("Some name");
        updateItemRequestDto.setPrice(BigDecimal.valueOf(120.57));

        Long nonExistentId = 59L;

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                service.updateItemById(nonExistentId, updateItemRequestDto));

        assertEquals("Item with id " + nonExistentId + " not found", exception.getMessage());
    }

    @Test
    void testDeleteItemById() {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setName("Some name");
        itemRequestDto.setPrice(BigDecimal.valueOf(120.57));

        ItemResponseDto createdItemResponseDto = service.createItem(itemRequestDto);

        service.deleteItemById(createdItemResponseDto.getId());

        EntityNotFoundException exception = org.junit.jupiter.api.Assertions.assertThrows(EntityNotFoundException.class,
                () -> service.getItemById(createdItemResponseDto.getId()));

        assertEquals("Item with id " + createdItemResponseDto.getId() + " not found", exception.getMessage());
    }
}
