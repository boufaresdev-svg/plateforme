package com.example.BacK.infrastructure.repository.g_Stock;

import com.example.BacK.domain.g_Stock.AjustementStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AjustementStockRepository extends JpaRepository<AjustementStock, String> {
    
    @Query("SELECT a FROM AjustementStock a WHERE a.article.id = :articleId ORDER BY a.dateAjustement DESC")
    List<AjustementStock> findByArticleId(@Param("articleId") String articleId);
    
    @Query("SELECT a FROM AjustementStock a WHERE a.utilisateur.id = :utilisateurId ORDER BY a.dateAjustement DESC")
    List<AjustementStock> findByUtilisateurId(@Param("utilisateurId") String utilisateurId);
    
    @Query("SELECT a FROM AjustementStock a WHERE a.dateAjustement BETWEEN :startDate AND :endDate ORDER BY a.dateAjustement DESC")
    List<AjustementStock> findByDateAjustementBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
