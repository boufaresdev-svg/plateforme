package com.example.BacK.application.g_Formation.Query.Domaine;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetDomaineResponse {

    private Long idDomaine;
    private String nom;
    private String description;



}
