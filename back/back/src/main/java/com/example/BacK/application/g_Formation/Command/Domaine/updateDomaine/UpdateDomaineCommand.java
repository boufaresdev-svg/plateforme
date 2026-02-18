package com.example.BacK.application.g_Formation.Command.Domaine.updateDomaine;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateDomaineCommand {

    private Long idDomaine;
    private String nom;
    private String description;
}
