package com.innowise.order.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity that stores an entry of an {@code Item} within a singular {@code Order}.
 */
@Entity
@Table(name = "order_items")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem {

    /**
     * Unique identifier of the record about a singular {@code Item} entry. Generates automatically by the database.
     */
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Unique identifier of the {@code Order}that owns the entry.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    /**
     * Unique identifier of the {@code Item} that is the entry within the order.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    /**
     * The amount of similar {@code Item}s within the {@code Order}.
     */
    @Column(nullable = false)
    private Integer quantity;
}
