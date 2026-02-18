package com.example.BacK.infrastructure.g_Stock.Repositories;

import com.example.BacK.domain.g_Stock.StockLot;
import com.example.BacK.domain.g_Stock.StockLotMouvement;
import com.example.BacK.domain.g_Stock.enumEntity.TypeMouvement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StockLotMouvementRepository extends JpaRepository<StockLotMouvement, String> {
    
    List<StockLotMouvement> findByStockLot(StockLot stockLot);
    
    List<StockLotMouvement> findByStockLotOrderByDateMouvementDesc(StockLot stockLot);
    
    List<StockLotMouvement> findByTypeMouvement(TypeMouvement typeMouvement);
    
    @Query("SELECT slm FROM StockLotMouvement slm WHERE slm.stockLot.id = :stockLotId ORDER BY slm.dateMouvement DESC")
    List<StockLotMouvement> findHistoryByStockLotId(@Param("stockLotId") String stockLotId);
    
    @Query("SELECT slm FROM StockLotMouvement slm WHERE slm.stockLot.article.id = :articleId ORDER BY slm.dateMouvement DESC")
    List<StockLotMouvement> findHistoryByArticleId(@Param("articleId") String articleId);
    
    @Query("SELECT slm FROM StockLotMouvement slm WHERE slm.dateMouvement BETWEEN :startDate AND :endDate ORDER BY slm.dateMouvement DESC")
    List<StockLotMouvement> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT slm FROM StockLotMouvement slm WHERE slm.stockLot.article.id = :articleId AND slm.stockLot.entrepot.id = :entrepotId ORDER BY slm.dateMouvement DESC")
    List<StockLotMouvement> findByArticleAndEntrepot(@Param("articleId") String articleId, @Param("entrepotId") String entrepotId);
}
