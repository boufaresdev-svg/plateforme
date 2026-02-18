package com.example.BacK.application.g_Stock.Query.stockLot;

import lombok.Data;

import java.time.LocalDate;

@Data
public class GetStockLotQuery {
    private String id;
    private String articleId;
    private String entrepotId;
    private Boolean estActif;
    private Boolean availableOnly;
    private LocalDate expiringBefore;
}
