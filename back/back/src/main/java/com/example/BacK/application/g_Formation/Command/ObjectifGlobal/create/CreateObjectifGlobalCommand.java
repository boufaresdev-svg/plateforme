package com.example.BacK.application.g_Formation.Command.ObjectifGlobal.create;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateObjectifGlobalCommand {
    private Long formationId;  // REQUIRED: Formation to link to
    private String libelle;
    private String description;
    private String tags;
}
