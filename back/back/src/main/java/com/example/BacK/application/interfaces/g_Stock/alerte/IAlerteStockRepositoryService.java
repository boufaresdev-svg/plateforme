package com.example.BacK.application.interfaces.g_Stock.alerte;

import com.example.BacK.domain.g_Stock.AlerteStock;
import com.example.BacK.domain.g_Stock.enumEntity.AlertePriorite;
import com.example.BacK.domain.g_Stock.enumEntity.AlerteStatut;
import com.example.BacK.domain.g_Stock.enumEntity.AlerteType;

import java.util.List;

public interface IAlerteStockRepositoryService {
    
    AlerteStock add(AlerteStock alerte);
    
    AlerteStock update(AlerteStock alerte);
    
    void delete(String id);
    
    AlerteStock getById(String id);
    
    List<AlerteStock> getAll();
    
    List<AlerteStock> getByStatut(AlerteStatut statut);
    
    List<AlerteStock> getByType(AlerteType type);
    
    List<AlerteStock> getByPriorite(AlertePriorite priorite);
    
    List<AlerteStock> getUnread();
    
    List<AlerteStock> getActive();
    
    List<AlerteStock> searchAlerts(AlerteType type, AlertePriorite priorite, AlerteStatut statut, Boolean isRead);
    
    Long countByStatut(AlerteStatut statut);
    
    Long countUnread();
    
    void generateStockAlerts();
}
