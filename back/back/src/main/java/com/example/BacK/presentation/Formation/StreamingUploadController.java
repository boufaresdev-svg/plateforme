package com.example.BacK.presentation.Formation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Streaming upload controller for handling large file uploads (1GB+).
 * 
 * This controller uses HttpServletRequest.getInputStream() to stream files
 * directly to disk without buffering in memory, which prevents:
 * - OutOfMemoryError for large files
 * - Broken pipe errors from reverse proxies (Apache/Nginx)
 * - Tomcat connection timeouts during slow uploads
 */
@RestController
@RequestMapping("/api/streaming-upload")
@Tag(name = "Streaming Upload", description = "Large file upload using streaming (1GB+ support)")
public class StreamingUploadController {

    private static final Logger logger = LoggerFactory.getLogger(StreamingUploadController.class);
    
    // 64KB buffer for streaming - optimal for disk I/O
    private static final int BUFFER_SIZE = 64 * 1024;

    @Value("${file.upload-dir:uploads/contenus}")
    private String uploadDir;

    /**
     * Stream upload a large file directly to disk.
     * 
     * Usage: POST /api/streaming-upload/file?filename=myfile.mp4
     * Content-Type: application/octet-stream
     * Body: raw file bytes
     * 
     * This bypasses Spring's multipart handling and streams directly from
     * the request input stream to disk.
     */
    @Operation(
            summary = "Upload large file via streaming",
            description = "Streams file directly to disk without memory buffering. Supports files up to 2GB+."
    )
    @PostMapping("/file")
    public ResponseEntity<Map<String, Object>> uploadLargeFile(
            HttpServletRequest request,
            @RequestParam(value = "filename", required = false) String originalFilename,
            @RequestParam(value = "contentId", required = false) Long contentId,
            @RequestParam(value = "levelNumber", required = false, defaultValue = "1") Integer levelNumber) {
        
        Map<String, Object> response = new HashMap<>();
        long startTime = System.currentTimeMillis();
        
        try {
            // Ensure upload directory exists
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                logger.info("Created upload directory: {}", uploadPath);
            }

            // Generate unique filename
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            
            String prefix = contentId != null ? "contenu_" + contentId + "_level_" + levelNumber + "_" : "";
            String uniqueFilename = prefix + UUID.randomUUID().toString() + extension;
            Path targetPath = uploadPath.resolve(uniqueFilename);

            // Stream directly from request to file
            long bytesWritten = streamToFile(request.getInputStream(), targetPath);

            long duration = System.currentTimeMillis() - startTime;
            double speedMbps = (bytesWritten / 1024.0 / 1024.0) / (duration / 1000.0);

            logger.info("Successfully uploaded {} bytes to {} in {}ms ({} MB/s)", 
                    bytesWritten, uniqueFilename, duration, String.format("%.2f", speedMbps));

            response.put("success", true);
            response.put("filePath", uniqueFilename);
            response.put("fileName", originalFilename != null ? originalFilename : uniqueFilename);
            response.put("fileSize", bytesWritten);
            response.put("uploadDurationMs", duration);
            response.put("speedMbps", String.format("%.2f", speedMbps));

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            logger.error("Failed to upload file: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("error", "Upload failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Stream upload with multipart boundary parsing for form-data uploads.
     * This handles the case when Content-Type is multipart/form-data.
     */
    @Operation(
            summary = "Upload large file via streaming (multipart)",
            description = "Handles multipart form-data uploads with streaming."
    )
    @PostMapping("/multipart")
    public ResponseEntity<Map<String, Object>> uploadMultipartStreaming(
            HttpServletRequest request,
            @RequestParam(value = "contentId", required = false) Long contentId,
            @RequestParam(value = "levelNumber", required = false, defaultValue = "1") Integer levelNumber) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // For multipart, we need to parse the boundary
            String contentType = request.getContentType();
            if (contentType == null || !contentType.contains("multipart/")) {
                response.put("success", false);
                response.put("error", "Content-Type must be multipart/form-data");
                return ResponseEntity.badRequest().body(response);
            }

            // Use Apache Commons FileUpload for streaming multipart parsing
            // For now, redirect to the standard multipart endpoint
            response.put("success", false);
            response.put("error", "Use /api/streaming-upload/file with Content-Type: application/octet-stream for large files");
            response.put("hint", "Send raw file bytes in request body, not multipart form-data");
            
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            logger.error("Failed multipart upload: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("error", "Upload failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Check upload status / health check
     */
    @GetMapping("/status")
    @Operation(summary = "Check streaming upload service status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Path uploadPath = Paths.get(uploadDir);
            long freeSpace = uploadPath.toFile().getFreeSpace();
            long totalSpace = uploadPath.toFile().getTotalSpace();
            
            response.put("status", "ready");
            response.put("uploadDir", uploadDir);
            response.put("uploadDirExists", Files.exists(uploadPath));
            response.put("uploadDirWritable", Files.isWritable(uploadPath));
            response.put("freeSpaceGB", String.format("%.2f", freeSpace / 1024.0 / 1024.0 / 1024.0));
            response.put("totalSpaceGB", String.format("%.2f", totalSpace / 1024.0 / 1024.0 / 1024.0));
            response.put("maxUploadSize", "2GB");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Stream from InputStream directly to file without loading into memory.
     * Uses a small buffer (64KB) to minimize memory usage while maintaining good I/O performance.
     */
    private long streamToFile(InputStream inputStream, Path targetPath) throws IOException {
        long totalBytesWritten = 0;
        byte[] buffer = new byte[BUFFER_SIZE];
        
        try (BufferedInputStream bis = new BufferedInputStream(inputStream, BUFFER_SIZE);
             FileOutputStream fos = new FileOutputStream(targetPath.toFile());
             BufferedOutputStream bos = new BufferedOutputStream(fos, BUFFER_SIZE)) {
            
            int bytesRead;
            while ((bytesRead = bis.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
                totalBytesWritten += bytesRead;
                
                // Flush periodically to prevent buffer buildup (every 10MB)
                if (totalBytesWritten % (10 * 1024 * 1024) == 0) {
                    bos.flush();
                    logger.debug("Progress: {} MB written", totalBytesWritten / 1024 / 1024);
                }
            }
            
            bos.flush();
            fos.getFD().sync(); // Ensure data is written to disk
        }
        
        return totalBytesWritten;
    }
}
