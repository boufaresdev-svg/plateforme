package com.example.BacK.application.g_Formation.Query.ObjectifSpecifique;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetObjectifSpecifiqueResponse {

    private Long idObjectifSpecifique;
    private String titre;
    private String description;
    private Long contenuGlobalid;
    private Long objectifGlobalId;

}
