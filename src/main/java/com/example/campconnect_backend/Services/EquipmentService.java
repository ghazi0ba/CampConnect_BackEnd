package com.example.campconnect_backend.Services;

import com.example.campconnect_backend.Dto.EquipmentDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface EquipmentService {
    public List<EquipmentDto.Response> getAll();
    EquipmentDto.Response create(EquipmentDto.CreateRequest dto);
    EquipmentDto.Response update(Long id, EquipmentDto.UpdateRequest dto);
    EquipmentDto.Response getById(Long id);
    void delete(Long id);
    Page<EquipmentDto.Response> search(EquipmentDto.Filter filter, Pageable pageable);
    EquipmentDto.Response setAvailability(Long id, boolean available);
}
