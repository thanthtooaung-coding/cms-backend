package com.content_management_system.bms.common.repository.jpa;

import com.content_management_system.bms.common.models.FoodOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoodOrderJpaRepository extends JpaRepository<FoodOrder, Long> {
    List<FoodOrder> findByUserEmail(String email);
}
