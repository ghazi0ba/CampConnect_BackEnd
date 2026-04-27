package com.example.campconnect_backend.Services;

import com.example.campconnect_backend.Dto.EquipmentDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EquipmentService {
    EquipmentDto.Response create(EquipmentDto.CreateRequest dto);
    EquipmentDto.Response update(Long id, EquipmentDto.UpdateRequest dto);
    EquipmentDto.Response patch(Long id, EquipmentDto.PatchRequest dto);
    EquipmentDto.Response getById(Long id);
    Page<EquipmentDto.Response> search(EquipmentDto.Filter filter, Pageable pageable);
    EquipmentDto.Response setAvailability(Long id, boolean available);
    EquipmentDto.Response adjustStock(Long id, EquipmentDto.StockAdjustmentRequest request);
    void delete(Long id); // soft delete
}
