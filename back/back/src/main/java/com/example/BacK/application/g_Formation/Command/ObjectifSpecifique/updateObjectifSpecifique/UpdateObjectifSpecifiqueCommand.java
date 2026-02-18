package com.example.BacK.application.g_Formation.Command.ObjectifSpecifique.updateObjectifSpecifique;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateObjectifSpecifiqueCommand {

    private Long idObjectifSpecifique;
    private String titre;
    private String description;
    private Long idContenuGlobal;
    private Long idObjectifGlobal;

}
