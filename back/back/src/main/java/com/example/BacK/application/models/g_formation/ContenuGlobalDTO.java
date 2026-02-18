package com.example.BacK.application.models.g_formation;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContenuGlobalDTO {

    private Long idContenuGlobal;
    private String titre;
    private String description;
    private Long idFormation;

}
