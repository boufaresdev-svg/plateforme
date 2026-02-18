package com.example.BacK.application.g_Stock.Command.Article;

import com.example.BacK.application.interfaces.g_Stock.article.IArticleRepositoryService;
import com.example.BacK.domain.g_Stock.Article;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UpdateArticleCommandValidator {
    
    private final IArticleRepositoryService repositoryService;
    
    public void validate(UpdateArticleCommand command) {
        if (command.getId() == null || command.getId().trim().isEmpty()) {
            throw new IllegalArgumentException("L'ID de l'article est obligatoire");
        }
        
        if (!repositoryService.existsById(command.getId())) {
            throw new IllegalArgumentException("Article non trouvé avec l'ID: " + command.getId());
        }
        
        if (command.getNom() == null || command.getNom().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de l'article est obligatoire");
        }
        
        if (command.getSku() != null && !command.getSku().isEmpty()) {
            Optional<Article> existingArticle = repositoryService.findBySku(command.getSku());
            if (existingArticle.isPresent() && !existingArticle.get().getId().equals(command.getId())) {
                throw new IllegalArgumentException("Un autre article avec ce SKU existe déjà");
            }
        }
        
        if (command.getCodebare() != null && !command.getCodebare().isEmpty()) {
            Optional<Article> existingArticle = repositoryService.findByCodebare(command.getCodebare());
            if (existingArticle.isPresent() && !existingArticle.get().getId().equals(command.getId())) {
                throw new IllegalArgumentException("Un autre article avec ce code-barres existe déjà");
            }
        }
        
        if (command.getPrixAchat() != null && command.getPrixAchat() < 0) {
            throw new IllegalArgumentException("Le prix d'achat ne peut pas être négatif");
        }
        
        if (command.getPrixVente() != null && command.getPrixVente() < 0) {
            throw new IllegalArgumentException("Le prix de vente ne peut pas être négatif");
        }
        
        if (command.getStockMinimum() != null && command.getStockMinimum() < 0) {
            throw new IllegalArgumentException("Le stock minimum ne peut pas être négatif");
        }
        
        if (command.getStockMaximum() != null && command.getStockMaximum() < 0) {
            throw new IllegalArgumentException("Le stock maximum ne peut pas être négatif");
        }
        
        if (command.getStockMinimum() != null && command.getStockMaximum() != null 
            && command.getStockMinimum() > command.getStockMaximum()) {
            throw new IllegalArgumentException("Le stock minimum ne peut pas être supérieur au stock maximum");
        }
    }
}
