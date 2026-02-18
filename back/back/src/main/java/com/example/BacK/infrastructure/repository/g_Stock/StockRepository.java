package com.example.BacK.infrastructure.repository.g_Stock;

import com.example.BacK.domain.g_Stock.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, String> {
    
    @Query("SELECT s FROM Stock s WHERE s.article.id = :articleId")
    List<Stock> findByArticleId(@Param("articleId") String articleId);
    
    @Query("SELECT s FROM Stock s WHERE s.entrepot.id = :entrepotId")
    List<Stock> findByEntrepotId(@Param("entrepotId") String entrepotId);
    
    @Query("SELECT s FROM Stock s WHERE s.article.id = :articleId AND s.entrepot.id = :entrepotId")
    Optional<Stock> findByArticleIdAndEntrepotId(@Param("articleId") String articleId, @Param("entrepotId") String entrepotId);
    
    @Query("SELECT s FROM Stock s WHERE s.quantite <= :seuil")
    List<Stock> findStocksFaibles(@Param("seuil") Integer seuil);
    
    @Query("SELECT s FROM Stock s WHERE s.dateDexpiration IS NOT NULL AND s.dateDexpiration <= CURRENT_TIMESTAMP")
    List<Stock> findStocksExpires();
    
    @Query("SELECT COALESCE(SUM(s.quantite), 0) FROM Stock s WHERE s.article.id = :articleId")
    Integer getTotalQuantiteByArticleId(@Param("articleId") String articleId);
    
    @Query("SELECT COUNT(DISTINCT s.article.id) FROM Stock s WHERE s.entrepot.id = :entrepotId")
    Long countDistinctArticlesByEntrepot(@Param("entrepotId") String entrepotId);
}
