package com.example.BacK.application.g_Projet.Query.LivrableTache;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GetLivrableTacheQuery {
    private String id;
    private String nom;
    private String description;

}
