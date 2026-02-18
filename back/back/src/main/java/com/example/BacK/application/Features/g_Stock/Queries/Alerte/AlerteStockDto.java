package com.example.BacK.application.Features.g_Stock.Queries.Alerte;

import com.example.BacK.domain.g_Stock.enumEntity.AlertePriorite;
import com.example.BacK.domain.g_Stock.enumEntity.AlerteStatut;
import com.example.BacK.domain.g_Stock.enumEntity.AlerteType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AlerteStockDto {
    private String id;
    private AlerteType type;
    private AlertePriorite priorite;
    private AlerteStatut statut;
    private String titre;
    private String message;
    private String description;
    private String articleId;
    private String articleNom;
    private String articleSku;
    private String entrepotId;
    private String entrepotNom;
    private Integer quantiteActuelle;
    private Integer seuilMinimum;
    private Integer seuilCritique;
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;
    private Boolean isRead;
    private Boolean isArchived;
}
