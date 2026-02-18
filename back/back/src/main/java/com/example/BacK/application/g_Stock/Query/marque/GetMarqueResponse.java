package com.example.BacK.application.g_Stock.Query.marque;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetMarqueResponse {
    
    private String id;
    private String nom;
    private String codeMarque;
    private String description;
    private String pays;
    private String siteWeb;
    private String urlLogo;
    private String nomContact;
    private String email;
    private String telephone;
    private String poste;
    private Boolean estActif;
    private Long nombreProduits;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
