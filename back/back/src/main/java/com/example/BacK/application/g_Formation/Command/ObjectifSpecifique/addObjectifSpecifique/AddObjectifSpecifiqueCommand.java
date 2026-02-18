package com.example.BacK.application.g_Formation.Command.ObjectifSpecifique.addObjectifSpecifique;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddObjectifSpecifiqueCommand {

    private String titre;
    private String description;
    private Long idContenuGlobal;
    private Long idObjectifGlobal;
}
