package com.example.campconnect_backend.Repositories;



import com.example.campconnect_backend.Entities.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EquipmentRepository extends JpaRepository<Equipment, Long> {
    List<Equipment> findByAvailableTrue();
}

