package com.example.campconnect_backend.Controllers;
import com.example.campconnect_backend.Dto.GroupMatchDto;
import com.example.campconnect_backend.Services.GroupMatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class GroupMatchController {

    private final GroupMatchService service;

    @PostMapping
    public ResponseEntity<GroupMatchDto.Response> create(@RequestBody GroupMatchDto.Request req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(req));
    }

    @GetMapping
    public ResponseEntity<List<GroupMatchDto.Response>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GroupMatchDto.Response> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/sport/{sport}")
    public ResponseEntity<List<GroupMatchDto.Response>> getBySport(@PathVariable String sport) {
        return ResponseEntity.ok(service.getBySport(sport));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GroupMatchDto.Response> update(@PathVariable Long id,
                                                         @RequestBody GroupMatchDto.Request req) {
        return ResponseEntity.ok(service.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}