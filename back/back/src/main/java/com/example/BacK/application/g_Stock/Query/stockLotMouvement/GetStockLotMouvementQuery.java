package com.example.BacK.application.g_Stock.Query.stockLotMouvement;

import lombok.Data;

@Data
public class GetStockLotMouvementQuery {
    private String id;
    private String stockLotId;
    private String articleId;
}
