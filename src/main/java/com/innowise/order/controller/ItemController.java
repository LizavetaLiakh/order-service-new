package com.innowise.order.controller;

import com.innowise.order.dto.ItemRequestDto;
import com.innowise.order.dto.ItemResponseDto;
import com.innowise.order.service.ItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST-controller for item management.
 * <p>
 * Provides CRUD-operations:
 * <ul>
 *     <li>Creating a new item</li>
 *     <li>Getting an item by id</li>
 *     <li>Getting a list of items by their ids</li>
 *     <li>Updating an item by id</li>
 *     <li>Deleting an item by id</li>
 * </ul>
 */
@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService service;

    public ItemController(ItemService service) {
        this.service = service;
    }

    /**
     * Creates a new item.
     *
     * @param itemDto New item's data.
     * @return Created item.
     * @response 201 Created - New item successfully created.
     * @response 500 Internal Server Error - Unexpected server error occurred.
     */
    @PostMapping("/add")
    public ResponseEntity<ItemResponseDto> addItem(@RequestBody ItemRequestDto itemDto) {
        ItemResponseDto newItem = service.createItem(itemDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(newItem);
    }

    /**
     * Finds an item by id.
     *
     * @param id Item's id.
     * @return Found item.
     * @throws com.innowise.order.exception.EntityNotFoundException If there's no item with given id.
     * @response 200 OK - Item found.
     * @response 404 Not Found - Item not found.
     * @response 500 Internal Server Error - Unexpected server error occurred.
     */
    @GetMapping("/get/{id}")
    public ResponseEntity<ItemResponseDto> getItemById(@PathVariable Long id) {
        ItemResponseDto item = service.getItemById(id);
        return ResponseEntity.status(HttpStatus.OK).body(item);
    }

    /**
     * Finds items by their ids.
     *
     * @param ids A list of items' ids.
     * @return A list of found items.
     * @throws com.innowise.order.exception.EmptyEntityListException If there's no items with given ids.
     * @response 200 OK - Items found.
     * @response 404 Not Found - Items not found.
     * @response 500 Internal Server Error - Unexpected server error occurred.
     */
    @GetMapping("/get")
    public ResponseEntity<List<ItemResponseDto>> getItemsByIds(@RequestParam List<Long> ids) {
        List<ItemResponseDto> items = service.getItemsByIds(ids);
        return ResponseEntity.status(HttpStatus.OK).body(items);
    }

    /**
     * Updates an item with given id.
     *
     * @param id Identifier of the item that should be updated.
     * @param itemDto New data of the item.
     * @throws com.innowise.order.exception.EntityNotFoundException If there's no item with given id.
     * @response 200 OK - Item successfully updated.
     * @response 404 Not Found - Item not found.
     * @response 500 Internal Server Error - Unexpected server error occurred.
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<ItemResponseDto> updateItem(@PathVariable Long id, @RequestBody ItemRequestDto itemDto) {
        ItemResponseDto updatedItem = service.updateItemById(id, itemDto);
        return ResponseEntity.status(HttpStatus.OK).body(updatedItem);
    }

    /**
     * Deletes an item with given id.
     *
     * @param id Identifier of the item that should be deleted.
     * @throws com.innowise.order.exception.EntityNotFoundException If there's no item with given id.
     * @response 204 No Content - Item successfully deleted.
     * @response 404 Not Found - Item not found.
     * @response 500 Internal Server Error - Unexpected server error occurred.
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        service.deleteItemById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
