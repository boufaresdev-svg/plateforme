package com.example.BacK.application.g_Formation.Command.ContenuDetaille.deleteContenuDetaille;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Command to delete a ContenuDetaille
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeleteContenuDetailleCommand {

    private Long idContenuDetaille; // Required: ID to delete
}
