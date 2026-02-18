package com.example.BacK.application.g_Stock.Command.ajustementStock.add;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddAjustementStockCommand {
    
    @NotBlank(message = "L'ID de l'article est obligatoire")
    private String articleId;
    
    private String entrepotId;
    
    @NotNull(message = "La quantité avant est obligatoire")
    private Integer quantiteAvant;
    
    @NotNull(message = "La quantité après est obligatoire")
    private Integer quantiteApres;
    
    private LocalDate dateAjustement;
    
    private String raison;
    
    private String notes;
}
