package com.example.BacK.application.interfaces.g_Stock.stockLotMouvement;

import com.example.BacK.domain.g_Stock.StockLotMouvement;
import com.example.BacK.domain.g_Stock.enumEntity.TypeMouvement;

import java.time.LocalDateTime;
import java.util.List;

public interface IStockLotMouvementRepositoryService {
    StockLotMouvement add(StockLotMouvement stockLotMouvement);
    StockLotMouvement update(StockLotMouvement stockLotMouvement);
    void delete(String id);
    StockLotMouvement getById(String id);
    List<StockLotMouvement> getAll();
    List<StockLotMouvement> getByStockLotId(String stockLotId);
    List<StockLotMouvement> getHistoryByStockLotId(String stockLotId);
    List<StockLotMouvement> getHistoryByArticleId(String articleId);
    List<StockLotMouvement> getByTypeMouvement(TypeMouvement typeMouvement);
    List<StockLotMouvement> getByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    List<StockLotMouvement> getByArticleAndEntrepot(String articleId, String entrepotId);
}
