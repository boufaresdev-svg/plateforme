package com.example.BacK.application.models.g_formation;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FormateurDTO {

    private Long idFormateur;
    private String nom;
    private String prenom;
    private String specialite;
    private String contact;
    private String experience;
    private String documentUrl;

}
