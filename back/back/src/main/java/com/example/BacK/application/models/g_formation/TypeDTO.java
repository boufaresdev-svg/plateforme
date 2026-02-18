package com.example.BacK.application.models.g_formation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TypeDTO {

    private Long idType;
    private String nom;
    private String description;
    private Long idDomaine;


}
