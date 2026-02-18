package com.example.BacK.application.g_Formation.Command.Type.updateType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateTypeCommand {

    private Long idType;
    private String nom;
    private String description;
    private Long domaineId;

}
