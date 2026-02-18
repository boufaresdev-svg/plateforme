package com.example.BacK.application.g_Stock.Command.stock.deleteStock;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeleteStockCommand {
    
    @NotBlank(message = "L'ID du stock est obligatoire")
    private String id;
}