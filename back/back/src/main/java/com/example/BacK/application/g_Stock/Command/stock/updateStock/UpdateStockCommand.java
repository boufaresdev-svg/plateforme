package com.example.BacK.application.g_Stock.Command.stock.updateStock;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateStockCommand {
    
    @NotBlank(message = "L'ID du stock est obligatoire")
    private String id;
    
    private String fournisseurId;
    
    @Min(value = 0, message = "La quantité ne peut pas être négative")
    private Integer quantite;
    
    private LocalDateTime dateDexpiration;
    
    private String updatedBy;
}