package com.innowise.order.repository;

import com.innowise.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrderId(Long orderId);
    List<OrderItem> findByItemId(Long itemId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE order_items SET order_id = :orderId, item_id = :itemId, quantity = :quantity " +
            "WHERE id = :id", nativeQuery = true)
    int updateOrderItem(@Param("id") Long id, @Param("orderId") Long orderId, @Param("itemId") Long itemId,
                    @Param("quantity") Integer quantity);

}
