package com.example.BacK.application.g_Projet.Command.ProblemeTache.add;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AddProblemeTacheCommand {
     private String nom;
    private String tacheId;
    private String description;


}
