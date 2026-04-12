package com.example.campconnect_backend.Services;

import com.example.campconnect_backend.Entities.Equipment;
import com.example.campconnect_backend.Repositories.EquipmentRepository;
import com.example.campconnect_backend.Services.EquipmentService;
import com.example.campconnect_backend.Dto.EquipmentDto;
import com.example.campconnect_backend.Services.EquipmentSpecifications;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EquipmentServiceImpl implements EquipmentService {

    private final EquipmentRepository equipmentRepository;

    @Override
    public EquipmentDto.Response create(EquipmentDto.CreateRequest dto) {
        validatePriceRange(dto.getPrice(), null);

        Equipment equipment = Equipment.builder()
                .name(dto.getName().trim())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .available(dto.getAvailable())
                .subCategory(dto.getSubCategory())
                // category is auto-synced by @PrePersist/@PreUpdate
                .build();

        Equipment saved = equipmentRepository.save(equipment);
        return toResponse(saved);
    }

    @Override
    public EquipmentDto.Response update(Long id, EquipmentDto.UpdateRequest dto) {
        validatePriceRange(dto.getPrice(), null);

        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Equipment not found with id: " + id));

        equipment.setName(dto.getName().trim());
        equipment.setDescription(dto.getDescription());
        equipment.setPrice(dto.getPrice());
        equipment.setAvailable(dto.getAvailable());
        equipment.setSubCategory(dto.getSubCategory());
        // category auto-sync handled in entity callback

        Equipment updated = equipmentRepository.save(equipment);
        return toResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public EquipmentDto.Response getById(Long id) {
        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Equipment not found with id: " + id));

        return toResponse(equipment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EquipmentDto.Response> getAll() {
        return equipmentRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }


    @Override
    public void delete(Long id) {
        if (!equipmentRepository.existsById(id)) {
            throw new EntityNotFoundException("Equipment not found with id: " + id);
        }
        equipmentRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EquipmentDto.Response> search(EquipmentDto.Filter filter, Pageable pageable) {
        if (filter == null) {
            filter = new EquipmentDto.Filter();
        }

        validatePriceRange(filter.getMinPrice(), filter.getMaxPrice());

        Specification<Equipment> spec = Specification
                .where(EquipmentSpecifications.keywordContains(filter.getKeyword()))
                .and(EquipmentSpecifications.hasCategory(filter.getCategory()))
                .and(EquipmentSpecifications.hasSubCategory(filter.getSubCategory()))
                .and(EquipmentSpecifications.isAvailable(filter.getAvailable()))
                .and(EquipmentSpecifications.priceGreaterOrEqual(filter.getMinPrice()))
                .and(EquipmentSpecifications.priceLessOrEqual(filter.getMaxPrice()));

        return equipmentRepository.findAll(spec, pageable).map(this::toResponse);
    }

    @Override
    public EquipmentDto.Response setAvailability(Long id, boolean available) {
        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Equipment not found with id: " + id));

        equipment.setAvailable(available);
        Equipment saved = equipmentRepository.save(equipment);

        return toResponse(saved);
    }

    private void validatePriceRange(Double min, Double max) {
        if (min != null && min < 0) {
            throw new IllegalArgumentException("min price must be >= 0");
        }
        if (max != null && max < 0) {
            throw new IllegalArgumentException("max price must be >= 0");
        }
        if (min != null && max != null && min > max) {
            throw new IllegalArgumentException("min price cannot be greater than max price");
        }
    }

    private EquipmentDto.Response toResponse(Equipment e) {
        return EquipmentDto.Response.builder()
                .id(e.getId())
                .name(e.getName())
                .description(e.getDescription())
                .price(e.getPrice())
                .available(e.getAvailable())
                .category(e.getCategory())
                .subCategory(e.getSubCategory())
                .build();
    }
}
