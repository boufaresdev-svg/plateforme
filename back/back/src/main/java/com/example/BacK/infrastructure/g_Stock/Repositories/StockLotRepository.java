package com.example.BacK.infrastructure.g_Stock.Repositories;

import com.example.BacK.domain.g_Stock.Article;
import com.example.BacK.domain.g_Stock.Entrepot;
import com.example.BacK.domain.g_Stock.StockLot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface StockLotRepository extends JpaRepository<StockLot, String> {
    
    List<StockLot> findByArticle(Article article);
    
    List<StockLot> findByArticleAndEntrepot(Article article, Entrepot entrepot);
    
    List<StockLot> findByEntrepot(Entrepot entrepot);
    
    List<StockLot> findByNumeroLot(String numeroLot);
    
    @Query("SELECT sl FROM StockLot sl WHERE sl.article.id = :articleId AND sl.entrepot.id = :entrepotId AND sl.quantiteActuelle > 0 AND sl.estActif = true ORDER BY sl.dateAchat ASC")
    List<StockLot> findAvailableLotsFIFO(@Param("articleId") String articleId, @Param("entrepotId") String entrepotId);
    
    @Query("SELECT sl FROM StockLot sl WHERE sl.article.id = :articleId AND sl.entrepot.id = :entrepotId AND sl.quantiteActuelle > 0 AND sl.estActif = true ORDER BY sl.dateAchat DESC")
    List<StockLot> findAvailableLotsLIFO(@Param("articleId") String articleId, @Param("entrepotId") String entrepotId);
    
    @Query("SELECT sl FROM StockLot sl WHERE sl.article.id = :articleId AND sl.entrepot.id = :entrepotId AND sl.estActif = true")
    List<StockLot> findByArticleIdAndEntrepotId(@Param("articleId") String articleId, @Param("entrepotId") String entrepotId);
    
    @Query("SELECT sl FROM StockLot sl WHERE sl.dateExpiration IS NOT NULL AND sl.dateExpiration <= :date AND sl.quantiteActuelle > 0 AND sl.estActif = true")
    List<StockLot> findExpiringBefore(@Param("date") LocalDate date);
    
    @Query("SELECT SUM(sl.quantiteActuelle) FROM StockLot sl WHERE sl.article.id = :articleId AND sl.entrepot.id = :entrepotId AND sl.estActif = true")
    Optional<Integer> getTotalQuantiteByArticleAndEntrepot(@Param("articleId") String articleId, @Param("entrepotId") String entrepotId);
    
    @Query("SELECT SUM(sl.quantiteActuelle * sl.prixAchatUnitaire) FROM StockLot sl WHERE sl.article.id = :articleId AND sl.entrepot.id = :entrepotId AND sl.estActif = true")
    Optional<Double> getTotalValueByArticleAndEntrepot(@Param("articleId") String articleId, @Param("entrepotId") String entrepotId);
    
    @Query("SELECT sl FROM StockLot sl WHERE sl.estActif = true AND (sl.quantiteActuelle - sl.quantiteReservee) > 0")
    List<StockLot> findAllWithAvailableQuantity();
}
