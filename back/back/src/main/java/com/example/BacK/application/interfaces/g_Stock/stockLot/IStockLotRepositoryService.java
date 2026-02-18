package com.example.BacK.application.interfaces.g_Stock.stockLot;

import com.example.BacK.domain.g_Stock.StockLot;

import java.time.LocalDate;
import java.util.List;

public interface IStockLotRepositoryService {
    StockLot add(StockLot stockLot);
    StockLot update(StockLot stockLot);
    void delete(String id);
    StockLot getById(String id);
    List<StockLot> getAll();
    List<StockLot> getByArticleId(String articleId);
    List<StockLot> getByEntrepotId(String entrepotId);
    List<StockLot> getByArticleIdAndEntrepotId(String articleId, String entrepotId);
    List<StockLot> getAvailableLotsFIFO(String articleId, String entrepotId);
    List<StockLot> getAvailableLotsLIFO(String articleId, String entrepotId);
    List<StockLot> getExpiringBefore(LocalDate date);
    Integer getTotalQuantiteByArticleAndEntrepot(String articleId, String entrepotId);
    Double getTotalValueByArticleAndEntrepot(String articleId, String entrepotId);
    List<StockLot> getAllWithAvailableQuantity();
}
