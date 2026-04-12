package com.example.campconnect_backend.Controllers;

import com.example.campconnect_backend.Dto.EquipmentDto;
import com.example.campconnect_backend.Services.EquipmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/equipment")
@RequiredArgsConstructor
public class EquipmentController {

    private final EquipmentService equipmentService;

    /**
     * Advanced search endpoint with pagination.
     * Example:
     * GET /api/equipment/search?keyword=tent&available=true&minPrice=10&maxPrice=200&page=0&size=10&sort=price,asc
     */
    @GetMapping("/search")
    public ResponseEntity<Page<EquipmentDto.Response>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) com.example.campconnect_backend.Entities.EquipmentCategory category,
            @RequestParam(required = false) com.example.campconnect_backend.Entities.EquipmentSubCategory subCategory,
            @RequestParam(required = false) Boolean available,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            Pageable pageable
    )
    {
        EquipmentDto.Filter filter = new EquipmentDto.Filter();
        filter.setKeyword(keyword);
        filter.setCategory(category);
        filter.setSubCategory(subCategory);
        filter.setAvailable(available);
        filter.setMinPrice(minPrice);
        filter.setMaxPrice(maxPrice);

        return ResponseEntity.ok(equipmentService.search(filter, pageable));
    }

    @GetMapping
    public ResponseEntity<List<EquipmentDto.Response>> getAll() {
        return ResponseEntity.ok(equipmentService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EquipmentDto.Response> getById(@PathVariable Long id) {
        return ResponseEntity.ok(equipmentService.getById(id));
    }

    @PostMapping
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EquipmentDto.Response> create(@Valid @RequestBody EquipmentDto.CreateRequest request) {
        return ResponseEntity.ok(equipmentService.create(request));
    }

    @PutMapping("/{id}")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EquipmentDto.Response> update(
            @PathVariable Long id,
            @Valid @RequestBody EquipmentDto.UpdateRequest request
    ) {
        return ResponseEntity.ok(equipmentService.update(id, request));
    }

    @PatchMapping("/{id}/availability")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EquipmentDto.Response> setAvailability(
            @PathVariable Long id,
            @RequestParam boolean available
    ) {
        return ResponseEntity.ok(equipmentService.setAvailability(id, available));
    }

    @DeleteMapping("/{id}")
   // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        equipmentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
