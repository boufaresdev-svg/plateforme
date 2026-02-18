package com.example.BacK.application.g_Stock.Command.marque.updateMarque;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateMarqueCommand {
    
    @NotBlank(message = "L'ID est obligatoire")
    private String id;
    
    @NotBlank(message = "Le nom de la marque est obligatoire")
    @Size(max = 100, message = "Le nom ne peut pas dépasser 100 caractères")
    private String nom;
    
    @Size(max = 50, message = "Le code marque ne peut pas dépasser 50 caractères")
    private String codeMarque;
    
    private String description;
    
    @Size(max = 100, message = "Le pays ne peut pas dépasser 100 caractères")
    private String pays;
    
    @Size(max = 255, message = "Le site web ne peut pas dépasser 255 caractères")
    private String siteWeb;
    
    @Size(max = 500, message = "L'URL du logo ne peut pas dépasser 500 caractères")
    private String urlLogo;
    
    @Size(max = 100, message = "Le nom du contact ne peut pas dépasser 100 caractères")
    private String nomContact;
    
    @Size(max = 150, message = "L'email ne peut pas dépasser 150 caractères")
    private String email;
    
    @Size(max = 20, message = "Le téléphone ne peut pas dépasser 20 caractères")
    private String telephone;
    
    @Size(max = 50, message = "Le poste ne peut pas dépasser 50 caractères")
    private String poste;
    
    private Boolean estActif;
}
