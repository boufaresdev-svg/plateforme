package com.example.BacK.application.g_Projet.Command.LivrableTache.update;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdateLivrableTacheCommand {
    private String id;
    private String nom;
    private String tacheId;
    private String description;


}
