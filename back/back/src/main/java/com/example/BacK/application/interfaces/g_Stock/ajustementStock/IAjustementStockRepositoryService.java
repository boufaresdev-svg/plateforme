package com.example.BacK.application.interfaces.g_Stock.ajustementStock;

import com.example.BacK.application.g_Stock.Query.ajustementStock.GetAjustementStockResponse;
import com.example.BacK.domain.g_Stock.AjustementStock;

import java.time.LocalDate;
import java.util.List;

public interface IAjustementStockRepositoryService {
    
    String add(AjustementStock ajustementStock);
    
    void update(AjustementStock ajustementStock);
    
    void delete(String id);
    
    AjustementStock getById(String id);
    
    List<GetAjustementStockResponse> getAll();
    
    List<GetAjustementStockResponse> getByArticleId(String articleId);
    
    List<GetAjustementStockResponse> getByUtilisateurId(String utilisateurId);
    
    List<GetAjustementStockResponse> getByDateRange(LocalDate startDate, LocalDate endDate);
}
