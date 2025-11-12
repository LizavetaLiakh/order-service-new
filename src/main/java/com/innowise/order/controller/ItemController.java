package com.innowise.order.controller;

import com.innowise.order.dto.ItemRequestDto;
import com.innowise.order.dto.ItemResponseDto;
import com.innowise.order.service.ItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService service;

    public ItemController(ItemService service) {
        this.service = service;
    }

    @PostMapping("/add")
    public ResponseEntity<ItemResponseDto> addItem(@RequestBody ItemRequestDto itemDto) {
        ItemResponseDto newItem = service.createItem(itemDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(newItem);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<ItemResponseDto> getItemById(@PathVariable Long id) {
        ItemResponseDto item = service.getItemById(id);
        return ResponseEntity.status(HttpStatus.OK).body(item);
    }

    @GetMapping("/get")
    public ResponseEntity<List<ItemResponseDto>> getItemsByIds(@RequestParam List<Long> ids) {
        List<ItemResponseDto> items = service.getItemsByIds(ids);
        return ResponseEntity.status(HttpStatus.OK).body(items);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ItemResponseDto> updateItem(@PathVariable Long id, @RequestBody ItemRequestDto itemDto) {
        ItemResponseDto updatedItem = service.updateItemById(id, itemDto);
        return ResponseEntity.status(HttpStatus.OK).body(updatedItem);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        service.deleteItemById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
