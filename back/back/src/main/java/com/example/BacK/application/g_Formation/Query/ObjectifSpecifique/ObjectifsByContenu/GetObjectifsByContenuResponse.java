package com.example.BacK.application.g_Formation.Query.ObjectifSpecifique.ObjectifsByContenu;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetObjectifsByContenuResponse {

    private Long idObjectifSpec;
    private String titre;
    private String description;

}
