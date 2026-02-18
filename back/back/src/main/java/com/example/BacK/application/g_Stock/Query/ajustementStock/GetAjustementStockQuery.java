package com.example.BacK.application.g_Stock.Query.ajustementStock;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetAjustementStockQuery {
    private String id;
    private String articleId;
    private String entrepotId;
    private String utilisateurId;
    private String categorieId;
    private String marqueId;
    private String fournisseurId;
    private LocalDate startDate;
    private LocalDate endDate;
}
