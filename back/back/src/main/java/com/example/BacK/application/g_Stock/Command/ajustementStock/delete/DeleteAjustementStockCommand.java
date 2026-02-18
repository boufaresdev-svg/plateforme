package com.example.BacK.application.g_Stock.Command.ajustementStock.delete;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeleteAjustementStockCommand {
    
    @NotBlank(message = "L'ID de l'ajustement est obligatoire")
    private String id;
}
