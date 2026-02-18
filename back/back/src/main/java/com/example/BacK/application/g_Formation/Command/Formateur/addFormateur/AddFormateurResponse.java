package com.example.BacK.application.g_Formation.Command.Formateur.addFormateur;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddFormateurResponse {

    private Long idFormateur;
    private String nom;
    private String prenom;
    private String specialite;
    private String contact;
    private String experience;
    private String documentUrl;
    private String message;
}
