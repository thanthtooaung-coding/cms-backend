package com.movie.celestix.common.repository.jpa;

import com.movie.celestix.common.models.FoodOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoodOrderJpaRepository extends JpaRepository<FoodOrder, Long> {
    List<FoodOrder> findByUserEmail(String email);
}
