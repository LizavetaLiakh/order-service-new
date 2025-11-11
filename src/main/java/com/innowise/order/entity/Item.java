package com.innowise.order.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

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
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    /**
     * List of records for orders where current item is used. Is not used in db schema.
     */
    @OneToMany(mappedBy = "itemId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems;
}
