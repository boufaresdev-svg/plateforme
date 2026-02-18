package com.example.BacK.application.g_Formation.Command.ContenuGlobal.updateContenuGlobal;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateContenuGlobalCommand {

    private Long idContenuGlobal;
    private String titre;
    private String description;
    private Long idFormation;

}
