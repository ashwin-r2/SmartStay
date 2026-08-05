package com.staysmart.property.service;

import com.staysmart.config.AppProperties;
import com.staysmart.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Stores uploaded images on the local filesystem under {@code app.uploads.dir}, returning a URL
 * served by {@link com.staysmart.config.WebConfig}'s static resource handler. Swappable for an
 * S3/Cloud Storage-backed implementation in production without changing any caller — only this
 * class and {@code app.uploads.*} config would change.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ImageStorageService {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of("image/jpeg", "image/png", "image/webp", "image/gif");

    private final AppProperties appProperties;

    public String store(MultipartFile file, String subDirectory) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Uploaded file is empty");
        }
        if (!ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {
            throw new BadRequestException("Only JPEG, PNG, WEBP or GIF images are allowed");
        }

        try {
            Path targetDir = Path.of(appProperties.getUploads().getDir(), subDirectory).toAbsolutePath().normalize();
            Files.createDirectories(targetDir);

            String extension = extractExtension(file.getOriginalFilename());
            String fileName = UUID.randomUUID() + extension;
            Path targetPath = targetDir.resolve(fileName);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            String url = appProperties.getUploads().getBaseUrl() + "/" + subDirectory + "/" + fileName;
            log.info("Stored uploaded image at {}", targetPath);
            return url;
        } catch (IOException e) {
            throw new BadRequestException("Failed to store uploaded file: " + e.getMessage());
        }
    }

    public List<String> storeAll(List<MultipartFile> files, String subDirectory) {
        return files.stream().map(f -> store(f, subDirectory)).toList();
    }

    private String extractExtension(String originalFilename) {
        if (originalFilename == null || !originalFilename.contains(".")) {
            return "";
        }
        return originalFilename.substring(originalFilename.lastIndexOf('.'));
    }
}
