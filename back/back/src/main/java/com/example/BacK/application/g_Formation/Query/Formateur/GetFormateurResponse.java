package com.example.BacK.application.g_Formation.Query.Formateur;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetFormateurResponse {

    private Long idFormateur;
    private String nom;
    private String prenom;
    private String specialite;
    private String contact;
    private String experience;
    private String documentUrl;
}
