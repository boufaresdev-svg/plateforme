package com.example.BacK.application.g_Formation.Command.Formateur.updateFormateur.UpdateFormateurWithFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateFormateurWithFileCommand  {

    private Long idFormateur;
    private String nom;
    private String prenom;
    private String specialite;
    private String contact;
    private String experience;
    private MultipartFile file;
}
