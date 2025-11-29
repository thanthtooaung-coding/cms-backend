package com.movie.celestix.common.repository.jpa;

import com.movie.celestix.common.models.FoodOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FoodOrderItemJpaRepository extends JpaRepository<FoodOrderItem, Long> {
}
