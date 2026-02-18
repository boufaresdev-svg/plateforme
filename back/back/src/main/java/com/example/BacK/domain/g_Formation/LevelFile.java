package com.example.BacK.domain.g_Formation;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Represents a file attachment for a content level
 * Each level can have multiple files
 */
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LevelFile {

    @Column(name = "file_name")
    private String fileName; // Original file name

    @Column(name = "file_path")
    private String filePath; // Server file path (unique identifier)

    @Column(name = "file_type")
    private String fileType; // MIME type

    @Column(name = "file_size")
    private Long fileSize; // File size in bytes

    @Column(name = "upload_date")
    private LocalDateTime uploadDate; // When the file was uploaded

    @Column(name = "description")
    private String description; // Optional file description
}
