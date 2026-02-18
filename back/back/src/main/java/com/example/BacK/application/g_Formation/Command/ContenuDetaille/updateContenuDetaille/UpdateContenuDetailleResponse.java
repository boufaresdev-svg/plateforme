package com.example.BacK.application.g_Formation.Command.ContenuDetaille.updateContenuDetaille;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateContenuDetailleResponse {

    private Long idContenuDetaille;
    private String titre;
    private String message;
}
