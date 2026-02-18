package com.example.BacK.application.g_Formation.Query.ContenuGlobal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetContenuGlobalResponse {

    private Long idContenuGlobal;
    private String titre;
    private String description;
    private Long Formationid;

}
