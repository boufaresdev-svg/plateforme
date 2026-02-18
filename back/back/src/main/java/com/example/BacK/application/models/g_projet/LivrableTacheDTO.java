package com.example.BacK.application.models.g_projet;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LivrableTacheDTO {
    private String id;
    private String nom;
    private TacheDTO tache;
    private String description;

}
