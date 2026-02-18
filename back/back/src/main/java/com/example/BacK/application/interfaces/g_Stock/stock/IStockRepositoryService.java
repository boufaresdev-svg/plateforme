package com.example.BacK.application.interfaces.g_Stock.stock;

import com.example.BacK.domain.g_Stock.Stock;

import java.util.List;

public interface IStockRepositoryService {
    Stock add(Stock stock);
    Stock update(Stock stock);
    void delete(String id);
    Stock getById(String id);
    List<Stock> getAll();
    List<Stock> getByArticleId(String articleId);
    List<Stock> getByEntrepotId(String entrepotId);
    Stock getByArticleIdAndEntrepotId(String articleId, String entrepotId);
    Stock findByArticleIdAndEntrepotId(String articleId, String entrepotId);
    List<Stock> getStocksFaibles(Integer seuil);
    List<Stock> getStocksExpires();
}
