package com.example.BacK.application.g_Stock.Query.mouvementStock;

import com.example.BacK.domain.g_Stock.enumEntity.Statut;
import com.example.BacK.domain.g_Stock.enumEntity.TypeMouvement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetMouvementStockQuery {
    
    private String id;
    private String articleId;
    private String entrepotId;
    private String utilisateurId;
    private String categorieId;
    private String marqueId;
    private TypeMouvement typeMouvement;
    private Statut statut;
    private String reference;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    
    // Pagination
    private Integer page;
    private Integer size;
    private String sortBy;
    private String sortDirection;
}
