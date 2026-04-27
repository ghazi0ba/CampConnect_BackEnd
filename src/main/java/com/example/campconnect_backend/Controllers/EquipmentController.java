package com.example.campconnect_backend.Controllers;

import com.example.campconnect_backend.Dto.EquipmentDto;
import com.example.campconnect_backend.Services.EquipmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/equipments")
@RequiredArgsConstructor
public class EquipmentController {

    private final EquipmentService equipmentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EquipmentDto.Response create(@Valid @RequestBody EquipmentDto.CreateRequest request) {
        return equipmentService.create(request);
    }

    @PutMapping("/{id}")
    public EquipmentDto.Response update(@PathVariable Long id, @Valid @RequestBody EquipmentDto.UpdateRequest request) {
        return equipmentService.update(id, request);
    }

    @PatchMapping("/{id}")
    public EquipmentDto.Response patch(@PathVariable Long id, @Valid @RequestBody EquipmentDto.PatchRequest request) {
        return equipmentService.patch(id, request);
    }

    @GetMapping("/{id}")
    public EquipmentDto.Response getById(@PathVariable Long id) {
        return equipmentService.getById(id);
    }

    @GetMapping
    public Page<EquipmentDto.Response> search(EquipmentDto.Filter filter, Pageable pageable) {
        return equipmentService.search(filter, pageable);
    }

    @PatchMapping("/{id}/availability")
    public EquipmentDto.Response setAvailability(@PathVariable Long id, @RequestParam boolean available) {
        return equipmentService.setAvailability(id, available);
    }

    @PatchMapping("/{id}/stock")
    public EquipmentDto.Response adjustStock(@PathVariable Long id,
                                             @Valid @RequestBody EquipmentDto.StockAdjustmentRequest request) {
        return equipmentService.adjustStock(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        equipmentService.delete(id);
    }
}
