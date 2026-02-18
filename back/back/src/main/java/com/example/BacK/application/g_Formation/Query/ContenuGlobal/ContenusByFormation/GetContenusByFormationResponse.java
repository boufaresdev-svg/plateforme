package com.example.BacK.application.g_Formation.Query.ContenuGlobal.ContenusByFormation;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetContenusByFormationResponse {

    private Long idContenuGlobal;
    private String titre;
    private String description;

}
