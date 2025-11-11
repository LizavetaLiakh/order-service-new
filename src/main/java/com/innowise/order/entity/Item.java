package com.innowise.order.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity that stores information about items.
 */
@Entity
@Table(name = "items")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Item {

    /**
     * Unique identifier of the item. Generates automatically by the database.
     */
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The name of the item.
     */
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * The price of the item. Stores with 2 decimal places.
     */
    @Column(name = "price", nullable = false)
    private double price;
}
