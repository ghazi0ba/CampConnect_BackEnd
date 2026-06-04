package com.example.campconnect_backend.controller;
 
import com.example.campconnect_backend.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
 
import java.util.HashMap;
import java.util.Map;
 
@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
@CrossOrigin // Important for frontend access
public class FileUploadController {
 
    private final FileStorageService fileStorageService;
 
    @PostMapping
    public ResponseEntity<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file) {
        String fileName = fileStorageService.storeFile(file);
 
        // We generate the public URL for the file
        // e.g. http://localhost:8080/uploads/uuid-original.jpg
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/uploads/")
                .path(fileName)
                .toUriString();
 
        Map<String, String> response = new HashMap<>();
        response.put("url", fileDownloadUri);
        response.put("fileName", fileName);
        response.put("status", "success");
 
        return ResponseEntity.ok(response);
    }
}
