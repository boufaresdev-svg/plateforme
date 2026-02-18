package com.example.BacK.application.interfaces.g_Stock.article;

import com.example.BacK.domain.g_Stock.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface IArticleRepositoryService {
    
    Article save(Article article);
    
    Article getById(String id);
    
    Optional<Article> findById(String id);
    
    Optional<Article> findByNom(String nom);
    
    Optional<Article> findBySku(String sku);
    
    Optional<Article> findByCodebare(String codebare);
    
    List<Article> findAll();
    
    List<Article> findByEstActifTrue();
    
    List<Article> findByEstActifFalse();
    
    List<Article> findByCategorieId(String categorieId);
    
    List<Article> findByMarqueId(String marqueId);
    
    List<Article> findByEstStockBaseeTrue();
    
    List<Article> findByEstStockEleverTrue();
    
    Page<Article> search(String searchTerm, String categorie, String marque, Boolean estActif, Pageable pageable);
    
    void deleteById(String id);
    
    boolean existsById(String id);
}
