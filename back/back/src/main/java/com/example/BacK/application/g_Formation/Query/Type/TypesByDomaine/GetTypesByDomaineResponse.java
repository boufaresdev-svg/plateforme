package com.example.BacK.application.g_Formation.Query.Type.TypesByDomaine;

import com.example.BacK.application.models.g_formation.DomaineDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetTypesByDomaineResponse {

        private Long idType;
        private String nom;
        private String description;
        private DomaineDTO domaine;

    }


