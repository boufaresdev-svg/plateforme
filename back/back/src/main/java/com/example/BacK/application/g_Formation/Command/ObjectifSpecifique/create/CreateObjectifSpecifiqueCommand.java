package com.example.BacK.application.g_Formation.Command.ObjectifSpecifique.create;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateObjectifSpecifiqueCommand {
    private Long formationId;  // REQUIRED: Formation to link to
    private String titre;
    private String description;
    private String tags;
}
