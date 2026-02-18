package com.example.BacK.application.g_Projet.Query.ProblemeTache;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GetProblemeTacheQuery {
    private String id;
    private String nom;
    private String description;

}
