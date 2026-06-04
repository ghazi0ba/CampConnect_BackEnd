package com.example.campconnect_backend.service;
 
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
 
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;
 
@Service
public class FileStorageService {
 
    private final Path fileStorageLocation;
 
    public FileStorageService() {
        // We store in an 'uploads' directory relative to the project root
        this.fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();
 
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }
 
    public String storeFile(MultipartFile file) {
        // Normalize file name
        String originalFileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String extension = "";
 
        try {
            if (originalFileName.contains("..")) {
                throw new RuntimeException("Sorry! Filename contains invalid path sequence " + originalFileName);
            }
 
            // Extract extension
            int lastDot = originalFileName.lastIndexOf('.');
            if (lastDot != -1) {
                extension = originalFileName.substring(lastDot);
            }
 
            // Generate unique filename to avoid collisions
            String fileName = UUID.randomUUID().toString() + extension;
 
            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
 
            return fileName;
        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + originalFileName + ". Please try again!", ex);
        }
    }
}
