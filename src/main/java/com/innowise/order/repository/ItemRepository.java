package com.innowise.order.repository;

import com.innowise.order.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    @Modifying
    @Transactional
    @Query(value = "UPDATE items SET name = :name, price = :price WHERE id = :id", nativeQuery = true)
    int updateItem(@Param("id") Long id, @Param("name") String name, @Param("price") Double price);

}
