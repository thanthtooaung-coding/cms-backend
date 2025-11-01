package com.content_management_system.bms.common.repository.jpa;

import com.content_management_system.bms.common.models.Combo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComboJpaRepository extends JpaRepository<Combo, Long> {
    boolean existsByFoods_Id(Long foodId);
    List<Combo> findByFoods_Id(Long foodId);
}
