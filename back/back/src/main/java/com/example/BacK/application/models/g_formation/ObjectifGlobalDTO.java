package com.example.BacK.application.models.g_formation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ObjectifGlobalDTO {
    private Long idObjectifGlobal;
    private String libelle;
    private String description;
    private String tags;
}
