package com.example.BacK.application.g_Formation.Command.Formation.duplicateFormation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DuplicateFormationResponse {
    private Long idFormation;
    private String theme;
    private String message;
}
