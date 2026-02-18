package com.example.BacK.application.g_Stock.Command.Article;

import com.example.BacK.application.interfaces.g_Stock.article.IArticleRepositoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AddArticleCommandValidator {
    
    private final IArticleRepositoryService repositoryService;
    
    public void validate(AddArticleCommand command) {
        // Basic validation is now handled by Jakarta Bean Validation annotations
        // We only need to check for uniqueness constraints and business rules
        
        if (command.getSku() != null && !command.getSku().trim().isEmpty()) {
            repositoryService.findBySku(command.getSku().trim())
                .ifPresent(article -> {
                    throw new IllegalArgumentException("Un article avec ce SKU existe déjà");
                });
        }
        
        if (command.getCodebare() != null && !command.getCodebare().trim().isEmpty()) {
            repositoryService.findByCodebare(command.getCodebare().trim())
                .ifPresent(article -> {
                    throw new IllegalArgumentException("Un article avec ce code-barres existe déjà");
                });
        }
        
        // Business rule: stock minimum should not exceed stock maximum
        if (command.getStockMinimum() != null && command.getStockMaximum() != null 
            && command.getStockMinimum() > command.getStockMaximum()) {
            throw new IllegalArgumentException("Le stock minimum ne peut pas être supérieur au stock maximum");
        }
    }
}
