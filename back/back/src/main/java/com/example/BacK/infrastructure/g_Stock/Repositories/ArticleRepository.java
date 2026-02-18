package com.example.BacK.infrastructure.g_Stock.Repositories;

import com.example.BacK.domain.g_Stock.Article;
import com.example.BacK.domain.g_Stock.Category;
import com.example.BacK.domain.g_Stock.Marque;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArticleRepository extends JpaRepository<Article, String> {
    
    Optional<Article> findByNom(String nom);
    
    Optional<Article> findBySku(String sku);
    
    Optional<Article> findByCodebare(String codebare);
    
    List<Article> findByCategorie(Category categorie);
    
    List<Article> findByMarque(Marque marque);
    
    List<Article> findByEstActifTrue();
    
    List<Article> findByEstActifFalse();
    
    List<Article> findByEstStockBaseeTrue();
    
    List<Article> findByEstStockEleverTrue();
    
    @Query("SELECT a FROM Article a WHERE " +
           "(:searchTerm IS NULL OR :searchTerm = '' OR " +
           "LOWER(a.nom) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(a.sku) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(a.codebare) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) AND " +
           "(:categorie IS NULL OR :categorie = '' OR a.categorie.id = :categorie) AND " +
           "(:marque IS NULL OR :marque = '' OR a.marque.id = :marque) AND " +
           "(:estActif IS NULL OR a.estActif = :estActif)")
    Page<Article> search(@Param("searchTerm") String searchTerm,
                         @Param("categorie") String categorie,
                         @Param("marque") String marque,
                         @Param("estActif") Boolean estActif,
                         Pageable pageable);
}
