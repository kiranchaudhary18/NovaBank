package com.novabank.backend.util;

import com.novabank.backend.exception.BadRequestException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Utility class to manage local file uploads, path resolutions, and input safety validations.
 *
 * @author Senior Java Backend Architect
 */
public final class FileUploadUtil {

    private static final String UPLOAD_DIR = "uploads";
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "pdf");
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5 MB

    private FileUploadUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Saves a multipart file into local storage within a specific sub-directory.
     * Enforces file size limitations and extension whitelist checks.
     *
     * @param subDir subfolder name (e.g. "photos", "kyc")
     * @param file the multipart file payload
     * @return the relative path location string of the stored file
     * @throws BadRequestException if the file fails safety checks
     */
    public static String saveFile(String subDir, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File upload failed: File is empty or missing.");
        }

        // 1. Verify File Size Limit
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BadRequestException("File upload failed: File size exceeds the maximum limit of 5MB.");
        }

        // 2. Validate File Extension
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new BadRequestException("File upload failed: Invalid file name format.");
        }

        String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new BadRequestException("File upload failed: Unsupported file extension. Allowed formats: " + ALLOWED_EXTENSIONS);
        }

        try {
            // Resolve upload path relative to project root
            Path uploadPath = Paths.get(UPLOAD_DIR, subDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Create a unique file name to avoid overwrite collisions
            String uniqueFileName = UUID.randomUUID().toString() + "_" + originalFilename.replaceAll("\\s+", "_");
            Path filePath = uploadPath.resolve(uniqueFileName);

            // Copy input stream to destination path
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Return path using forward slashes for URL mapping consistency
            return Paths.get(UPLOAD_DIR, subDir, uniqueFileName).toString().replace("\\", "/");
        } catch (IOException exception) {
            throw new RuntimeException("Local file storage operations encountered an error: " + exception.getMessage(), exception);
        }
    }
}
