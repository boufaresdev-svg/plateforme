package com.example.BacK.application.interfaces.g_Stock.mouvementStock;

import com.example.BacK.domain.g_Stock.MouvementStock;
import com.example.BacK.domain.g_Stock.enumEntity.Statut;
import com.example.BacK.domain.g_Stock.enumEntity.TypeMouvement;

import java.time.LocalDateTime;
import java.util.List;

public interface IMouvementStockRepositoryService {
    
    MouvementStock add(MouvementStock mouvementStock);
    
    MouvementStock update(MouvementStock mouvementStock);
    
    void delete(String id);
    
    MouvementStock getById(String id);
    
    List<MouvementStock> getAll();
    
    List<MouvementStock> getByArticleId(String articleId);
    
    List<MouvementStock> getByDestinationEntrepotId(String entrepotId);
    
    List<MouvementStock> getBySourceEntrepotId(String entrepotId);
    
    List<MouvementStock> getBySourceOrDestinationEntrepot(String entrepotId);
    
    List<MouvementStock> getByUtilisateurId(String utilisateurId);
    
    List<MouvementStock> getByTypeMouvement(TypeMouvement typeMouvement);
    
    List<MouvementStock> getByStatut(Statut statut);
    
    List<MouvementStock> getByDateMouvementBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    List<MouvementStock> getByReference(String reference);
}
