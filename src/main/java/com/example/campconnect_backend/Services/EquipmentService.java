package com.example.campconnect_backend.Services;

import com.example.campconnect_backend.Repositories.EquipmentRepository;
import com.example.campconnect_backend.Dto.EquipmentDto;
import com.example.campconnect_backend.Entities.Equipment;
import com.example.campconnect_backend.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;

    public List<EquipmentDto.Response> findAll() {
        return equipmentRepository.findAll().stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    public List<EquipmentDto.Response> findAvailable() {
        return equipmentRepository.findByAvailableTrue().stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    public EquipmentDto.Response findById(Long id) {
        return toResponse(getOrThrow(id));
    }

    public EquipmentDto.Response create(EquipmentDto.Request request) {
        Equipment eq = Equipment.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .available(request.getAvailable())
                .build();
        return toResponse(equipmentRepository.save(eq));
    }

    public EquipmentDto.Response update(Long id, EquipmentDto.Request request) {
        Equipment eq = getOrThrow(id);
        eq.setName(request.getName());
        eq.setDescription(request.getDescription());
        eq.setPrice(request.getPrice());
        eq.setAvailable(request.getAvailable());
        return toResponse(equipmentRepository.save(eq));
    }

    public void delete(Long id) {
        equipmentRepository.delete(getOrThrow(id));
    }

    private Equipment getOrThrow(Long id) {
        return equipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipment not found: " + id));
    }

    private EquipmentDto.Response toResponse(Equipment e) {
        EquipmentDto.Response dto = new EquipmentDto.Response();
        dto.setId(e.getId());
        dto.setName(e.getName());
        dto.setDescription(e.getDescription());
        dto.setPrice(e.getPrice());
        dto.setAvailable(e.getAvailable());
        return dto;
    }
}

