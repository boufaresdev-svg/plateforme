package com.example.BacK.application.g_Formation.Command.Formateur.updateFormateur;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateFormateurCommand {

    private Long idFormateur;
    private String nom;
    private String prenom;
    private String specialite;
    private String contact;
    private String experience;
    private String documentUrl;
}
