package com.example.BacK.application.g_Stock.Query.category;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetCategoryResponse {
    private String id;
    private String nom;
    private String description;
    private Boolean estActif;
    private Long nombreProduits;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}