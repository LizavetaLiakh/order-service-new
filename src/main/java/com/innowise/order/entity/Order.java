package com.innowise.order.entity;

import com.innowise.order.status.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * Entity that stores information about orders.
 */
@Entity
@Table(name = "orders")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {

    /**
     * Unique identifier of the order. Generates automatically by the database.
     */
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Unique identifier of the user from table "users" in mydb. The user who owns the card.
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * The status of the order.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    /**
     * The date when the order was created.
     */
    @Column(name = "creation_date", nullable = false)
    private LocalDate creationDate;

    /**
     * List of records for each item that composes current order. Is not used in db schema.
     */
    @OneToMany(mappedBy = "orderId", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems;
}
