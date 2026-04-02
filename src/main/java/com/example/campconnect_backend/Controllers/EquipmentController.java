package com.example.campconnect_backend.Controllers;



import com.example.campconnect_backend.Dto.EquipmentDto;
import com.example.campconnect_backend.Services.EquipmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/equipment")
@RequiredArgsConstructor
public class EquipmentController {

    private final EquipmentService equipmentService;

    @GetMapping
    public ResponseEntity<List<EquipmentDto.Response>> getAll(@RequestParam(required = false) Boolean available) {
        if (Boolean.TRUE.equals(available)) return ResponseEntity.ok(equipmentService.findAvailable());
        return ResponseEntity.ok(equipmentService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EquipmentDto.Response> getById(@PathVariable Long id) {
        return ResponseEntity.ok(equipmentService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EquipmentDto.Response> create(@Valid @RequestBody EquipmentDto.Request request) {
        return ResponseEntity.ok(equipmentService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EquipmentDto.Response> update(@PathVariable Long id,
                                                        @Valid @RequestBody EquipmentDto.Request request) {
        return ResponseEntity.ok(equipmentService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        equipmentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

