package com.example.campconnect_backend.Services;

import com.example.campconnect_backend.Dto.EquipmentDto;
import com.example.campconnect_backend.Entities.Equipment;
import com.example.campconnect_backend.exception.BusinessValidationException;
import com.example.campconnect_backend.exception.DuplicateResourceException;
import com.example.campconnect_backend.exception.ResourceNotFoundException;
import com.example.campconnect_backend.Repositories.EquipmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional
public class EquipmentServiceImpl implements EquipmentService {

    private final EquipmentRepository equipmentRepository;

    @Override
    public EquipmentDto.Response create(EquipmentDto.CreateRequest dto) {
        String normalizedName = normalizeName(dto.getName());
        validatePrice(dto.getPrice());
        validateStock(dto.getTotalQuantity(), dto.getReservedQuantity());

        if (equipmentRepository.existsByNameIgnoreCaseAndDeletedFalse(normalizedName)) {
            throw new DuplicateResourceException("Equipment name already exists: " + normalizedName);
        }

        Equipment equipment = Equipment.builder()
                .name(normalizedName)
                .description(normalizeDescription(dto.getDescription()))
                .price(dto.getPrice())
                .available(dto.getAvailable())
                .totalQuantity(dto.getTotalQuantity())
                .reservedQuantity(dto.getReservedQuantity())
                .subCategory(dto.getSubCategory())
                .build();

        return toResponse(equipmentRepository.save(equipment));
    }

    @Override
    public EquipmentDto.Response update(Long id, EquipmentDto.UpdateRequest dto) {
        Equipment equipment = getExisting(id);

        String normalizedName = normalizeName(dto.getName());
        validatePrice(dto.getPrice());
        validateStock(dto.getTotalQuantity(), dto.getReservedQuantity());

        if (equipmentRepository.existsByNameIgnoreCaseAndIdNotAndDeletedFalse(normalizedName, id)) {
            throw new DuplicateResourceException("Equipment name already exists: " + normalizedName);
        }

        equipment.setName(normalizedName);
        equipment.setDescription(normalizeDescription(dto.getDescription()));
        equipment.setPrice(dto.getPrice());
        equipment.setAvailable(dto.getAvailable());
        equipment.setTotalQuantity(dto.getTotalQuantity());
        equipment.setReservedQuantity(dto.getReservedQuantity());
        equipment.setSubCategory(dto.getSubCategory());

        try {
            return toResponse(equipmentRepository.save(equipment));
        } catch (OptimisticLockingFailureException e) {
            throw new BusinessValidationException("Concurrent update detected. Please refresh and retry.");
        }
    }

    @Override
    public EquipmentDto.Response patch(Long id, EquipmentDto.PatchRequest dto) {
        Equipment equipment = getExisting(id);

        if (dto.getName() != null) {
            String normalizedName = normalizeName(dto.getName());
            if (equipmentRepository.existsByNameIgnoreCaseAndIdNotAndDeletedFalse(normalizedName, id)) {
                throw new DuplicateResourceException("Equipment name already exists: " + normalizedName);
            }
            equipment.setName(normalizedName);
        }

        if (dto.getDescription() != null) {
            equipment.setDescription(normalizeDescription(dto.getDescription()));
        }
        if (dto.getPrice() != null) {
            validatePrice(dto.getPrice());
            equipment.setPrice(dto.getPrice());
        }
        if (dto.getAvailable() != null) {
            equipment.setAvailable(dto.getAvailable());
        }
        if (dto.getSubCategory() != null) {
            equipment.setSubCategory(dto.getSubCategory());
        }

        Integer total = dto.getTotalQuantity() != null ? dto.getTotalQuantity() : equipment.getTotalQuantity();
        Integer reserved = dto.getReservedQuantity() != null ? dto.getReservedQuantity() : equipment.getReservedQuantity();
        validateStock(total, reserved);

        if (dto.getTotalQuantity() != null) equipment.setTotalQuantity(dto.getTotalQuantity());
        if (dto.getReservedQuantity() != null) equipment.setReservedQuantity(dto.getReservedQuantity());

        try {
            return toResponse(equipmentRepository.save(equipment));
        } catch (OptimisticLockingFailureException e) {
            throw new BusinessValidationException("Concurrent update detected. Please refresh and retry.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public EquipmentDto.Response getById(Long id) {
        return toResponse(getExisting(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EquipmentDto.Response> search(EquipmentDto.Filter filter, Pageable pageable) {
        if (filter == null) filter = new EquipmentDto.Filter();

        validatePriceRange(filter.getMinPrice(), filter.getMaxPrice());

        Boolean deleted = Boolean.TRUE.equals(filter.getIncludeDeleted()) ? null : false;

        Specification<Equipment> spec = Specification
                .where(EquipmentSpecifications.keywordContains(filter.getKeyword()))
                .and(EquipmentSpecifications.hasCategory(filter.getCategory()))
                .and(EquipmentSpecifications.hasSubCategory(filter.getSubCategory()))
                .and(EquipmentSpecifications.isAvailable(filter.getAvailable()))
                .and(EquipmentSpecifications.inStock(filter.getInStock()))
                .and(EquipmentSpecifications.priceGreaterOrEqual(filter.getMinPrice()))
                .and(EquipmentSpecifications.priceLessOrEqual(filter.getMaxPrice()))
                .and(EquipmentSpecifications.isDeleted(deleted));

        return equipmentRepository.findAll(spec, pageable).map(this::toResponse);
    }

    @Override
    public EquipmentDto.Response setAvailability(Long id, boolean available) {
        Equipment equipment = getExisting(id);
        equipment.setAvailable(available);
        return toResponse(equipmentRepository.save(equipment));
    }

    @Override
    public EquipmentDto.Response adjustStock(Long id, EquipmentDto.StockAdjustmentRequest request) {
        Equipment equipment = getExisting(id);
        validateStock(request.getTotalQuantity(), request.getReservedQuantity());

        equipment.setTotalQuantity(request.getTotalQuantity());
        equipment.setReservedQuantity(request.getReservedQuantity());

        return toResponse(equipmentRepository.save(equipment));
    }

    @Override
    public void delete(Long id) {
        Equipment equipment = getExisting(id);
        equipment.setDeleted(true);
        equipmentRepository.save(equipment);
    }

    private Equipment getExisting(Long id) {
        return equipmentRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipment not found with id: " + id));
    }

    private void validatePrice(BigDecimal price) {
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessValidationException("Price must be greater than 0");
        }
    }

    private void validatePriceRange(BigDecimal min, BigDecimal max) {
        if (min != null && min.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessValidationException("min price must be >= 0");
        }
        if (max != null && max.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessValidationException("max price must be >= 0");
        }
        if (min != null && max != null && min.compareTo(max) > 0) {
            throw new BusinessValidationException("min price cannot be greater than max price");
        }
    }

    private void validateStock(Integer total, Integer reserved) {
        if (total == null || total < 0) {
            throw new BusinessValidationException("totalQuantity must be >= 0");
        }
        if (reserved == null || reserved < 0) {
            throw new BusinessValidationException("reservedQuantity must be >= 0");
        }
        if (reserved > total) {
            throw new BusinessValidationException("reservedQuantity cannot be greater than totalQuantity");
        }
    }

    private String normalizeName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new BusinessValidationException("Name is required");
        }
        return name.trim();
    }

    private String normalizeDescription(String description) {
        return description == null ? null : description.trim();
    }

    private EquipmentDto.Response toResponse(Equipment e) {
        return EquipmentDto.Response.builder()
                .id(e.getId())
                .version(e.getVersion())
                .name(e.getName())
                .description(e.getDescription())
                .price(e.getPrice())
                .available(e.getAvailable())
                .totalQuantity(e.getTotalQuantity())
                .reservedQuantity(e.getReservedQuantity())
                .availableQuantity(e.getAvailableQuantity())
                .inStock(e.isInStock())
                .category(e.getCategory())
                .subCategory(e.getSubCategory())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }
}
