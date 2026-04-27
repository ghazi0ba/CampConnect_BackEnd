package com.example.campconnect_backend.Repositories;

import com.example.campconnect_backend.Entities.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface EquipmentRepository extends JpaRepository<Equipment, Long>, JpaSpecificationExecutor<Equipment> {
    boolean existsByNameIgnoreCaseAndDeletedFalse(String name);
    boolean existsByNameIgnoreCaseAndIdNotAndDeletedFalse(String name, Long id);
    Optional<Equipment> findByIdAndDeletedFalse(Long id);
}
