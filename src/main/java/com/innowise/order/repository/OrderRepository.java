package com.innowise.order.repository;

import com.innowise.order.entity.Order;
import com.innowise.order.status.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserId(Long userId);
    List<Order> findByOrderStatus(OrderStatus orderStatus);

    @Modifying
    @Transactional
    @Query(value = "UPDATE orders SET user_id = :userId, status = :orderStatus, creation_date = :creationDate " +
            "WHERE id = :id", nativeQuery = true)
    int updateOrder(@Param("id") Long id, @Param("userId") Long userId, @Param("orderStatus") String orderStatus,
                       @Param("creationDate") LocalDate creationDate);

}
