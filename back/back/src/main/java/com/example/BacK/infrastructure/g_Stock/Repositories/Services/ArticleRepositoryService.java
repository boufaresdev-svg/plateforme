package com.example.BacK.infrastructure.g_Stock.Repositories.Services;

import com.example.BacK.application.interfaces.g_Stock.article.IArticleRepositoryService;
import com.example.BacK.application.interfaces.g_Stock.category.ICategoryRepositoryService;
import com.example.BacK.application.interfaces.g_Stock.marque.IMarqueRepositoryService;
import com.example.BacK.domain.g_Stock.Article;
import com.example.BacK.domain.g_Stock.Category;
import com.example.BacK.domain.g_Stock.Marque;
import com.example.BacK.infrastructure.g_Stock.Repositories.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ArticleRepositoryService implements IArticleRepositoryService {
    
    private final ArticleRepository articleRepository;
    private final ICategoryRepositoryService categoryRepositoryService;
    private final IMarqueRepositoryService marqueRepositoryService;
    
    @Override
    public Article save(Article article) {
        return articleRepository.save(article);
    }
    
    @Override
    public Article getById(String id) {
        return articleRepository.findById(id).orElse(null);
    }
    
    @Override
    public Optional<Article> findById(String id) {
        return articleRepository.findById(id);
    }
    
    @Override
    public Optional<Article> findByNom(String nom) {
        return articleRepository.findByNom(nom);
    }
    
    @Override
    public Optional<Article> findBySku(String sku) {
        return articleRepository.findBySku(sku);
    }
    
    @Override
    public Optional<Article> findByCodebare(String codebare) {
        return articleRepository.findByCodebare(codebare);
    }
    
    @Override
    public List<Article> findAll() {
        return articleRepository.findAll();
    }
    
    @Override
    public List<Article> findByEstActifTrue() {
        return articleRepository.findByEstActifTrue();
    }
    
    @Override
    public List<Article> findByEstActifFalse() {
        return articleRepository.findByEstActifFalse();
    }
    
    @Override
    public List<Article> findByCategorieId(String categorieId) {
        Category category = categoryRepositoryService.getById(categorieId);
        if (category == null) {
            return List.of();
        }
        return articleRepository.findByCategorie(category);
    }
    
    @Override
    public List<Article> findByMarqueId(String marqueId) {
        Marque marque = marqueRepositoryService.getById(marqueId);
        if (marque == null) {
            return List.of();
        }
        return articleRepository.findByMarque(marque);
    }
    
    @Override
    public List<Article> findByEstStockBaseeTrue() {
        return articleRepository.findByEstStockBaseeTrue();
    }
    
    @Override
    public List<Article> findByEstStockEleverTrue() {
        return articleRepository.findByEstStockEleverTrue();
    }
    
    @Override
    public Page<Article> search(String searchTerm, String categorie, String marque, Boolean estActif, Pageable pageable) {
        return articleRepository.search(searchTerm, categorie, marque, estActif, pageable);
    }
    
    @Override
    public void deleteById(String id) {
        articleRepository.deleteById(id);
    }
    
    @Override
    public boolean existsById(String id) {
        return articleRepository.existsById(id);
    }
}
