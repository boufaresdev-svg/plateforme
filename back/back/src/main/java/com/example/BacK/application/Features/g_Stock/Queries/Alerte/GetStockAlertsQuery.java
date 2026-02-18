package com.example.BacK.application.Features.g_Stock.Queries.Alerte;

import com.example.BacK.domain.g_Stock.enumEntity.AlertePriorite;
import com.example.BacK.domain.g_Stock.enumEntity.AlerteStatut;
import com.example.BacK.domain.g_Stock.enumEntity.AlerteType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetStockAlertsQuery {
    private AlerteType type;
    private AlertePriorite priorite;
    private AlerteStatut statut;
    private Boolean isRead;
    private Integer page;
    private Integer size;
    private String searchTerm;
}
