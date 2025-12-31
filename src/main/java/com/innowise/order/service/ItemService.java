package com.innowise.order.service;

import com.innowise.order.dto.ItemRequestDto;
import com.innowise.order.dto.ItemResponseDto;
import com.innowise.order.entity.Item;
import com.innowise.order.exception.EmptyItemListException;
import com.innowise.order.exception.ItemNotFoundException;
import com.innowise.order.mapper.ItemMapper;
import com.innowise.order.repository.ItemRepository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service class for managing Item entity.
 * <p>
 *     Provides CRUD operations: create, get item by id, get items by ids, update item by id, delete item by id.
 * </p>
 */
@Service
public class ItemService {

    private final ItemRepository repository;
    private final ItemMapper mapper;

    public ItemService(ItemRepository repository, ItemMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    /**
     * Creates a new item in the database.
     * @param itemDto DTO with new item's data
     * @return created item as DTO
     */
    public ItemResponseDto createItem(ItemRequestDto itemDto) {
        Item item = mapper.toItem(itemDto);
        Item savedItem = repository.save(item);
        return mapper.toItemResponseDto(savedItem);
    }

    /**
     * Finds an item by id.
     * @param id item's unique identifier
     * @return item as DTO if found, empty if not found
     */
    public ItemResponseDto getItemById(Long id) {
        return repository.findById(id)
                .map(mapper::toItemResponseDto)
                .orElseThrow(() -> new ItemNotFoundException(id));
    }

    /**
     * Finds items by ids.
     * @param ids list of items' unique identifiers
     * @return list of items as DTOs
     */
    public List<ItemResponseDto> getItemsByIds(Iterable<Long> ids) {
        List<ItemResponseDto> items = repository.findAllById(ids)
                .stream()
                .map(mapper::toItemResponseDto)
                .toList();
        if (items.isEmpty()) {
            throw new EmptyItemListException(ids);
        }
        return items;
    }

    /**
     * Updates an item by id.
     * @param id item's unique identifier
     * @param itemDto ItemRequestDto that contains new data
     */
    @Transactional
    public ItemResponseDto updateItemById(Long id, ItemRequestDto itemDto) {
        int updated = repository.updateItem(id, itemDto.getName(), itemDto.getPrice());
        if (updated == 0) {
            throw new ItemNotFoundException(id);
        }
        return repository.findById(id)
                .map(mapper::toItemResponseDto)
                .orElseThrow(() -> new ItemNotFoundException(id));
    }

    /**
     * Deletes an item by id.
     * @param id item's id
     */
    @Transactional
    public void deleteItemById(Long id) {
        try {
            repository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new ItemNotFoundException(id);
        }
    }
}
