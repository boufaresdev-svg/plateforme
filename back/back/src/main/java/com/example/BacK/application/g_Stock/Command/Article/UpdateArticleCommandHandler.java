package com.example.BacK.application.g_Stock.Command.Article;

import com.example.BacK.application.interfaces.g_Stock.article.IArticleRepositoryService;
import com.example.BacK.application.interfaces.g_Stock.category.ICategoryRepositoryService;
import com.example.BacK.application.interfaces.g_Stock.marque.IMarqueRepositoryService;
import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Stock.Article;
import com.example.BacK.domain.g_Stock.Category;
import com.example.BacK.domain.g_Stock.Marque;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UpdateArticleCommandHandler implements RequestHandler<UpdateArticleCommand, UpdateArticleCommandResponse> {
    
    private final IArticleRepositoryService repositoryService;
    private final ICategoryRepositoryService categoryRepositoryService;
    private final IMarqueRepositoryService marqueRepositoryService;
    
    @Override
    public UpdateArticleCommandResponse handle(UpdateArticleCommand command) {
        Article article = repositoryService.findById(command.getId())
            .orElseThrow(() -> new IllegalArgumentException("Article non trouvé avec l'ID: " + command.getId()));
        
        article.setSku(command.getSku());
        article.setCodebare(command.getCodebare());
        article.setNom(command.getNom());
        article.setDescription(command.getDescription());
        
        // Fetch and set Category entity
        if (command.getCategorieId() != null && !command.getCategorieId().isEmpty()) {
            Category categorie = categoryRepositoryService.getById(command.getCategorieId());
            if (categorie == null) {
                throw new IllegalArgumentException("Catégorie non trouvée avec l'ID: " + command.getCategorieId());
            }
            article.setCategorie(categorie);
        } else {
            article.setCategorie(null);
        }
        
        article.setImageUrl(command.getImageUrl());
        article.setUnitesDeMesure(command.getUnitesDeMesure());
        article.setPrixAchat(command.getPrixAchat());
        article.setPrixVente(command.getPrixVente());
        article.setTauxTaxe(command.getTauxTaxe());
        
        // Fetch and set Marque entity
        if (command.getMarqueId() != null && !command.getMarqueId().isEmpty()) {
            Marque marque = marqueRepositoryService.getById(command.getMarqueId());
            if (marque == null) {
                throw new IllegalArgumentException("Marque non trouvée avec l'ID: " + command.getMarqueId());
            }
            article.setMarque(marque);
        } else {
            article.setMarque(null);
        }
        
        article.setPrixVenteHT(command.getPrixVenteHT());
        article.setStockMinimum(command.getStockMinimum());
        article.setStockMaximum(command.getStockMaximum());
        
        article.setEstStockBasee(command.getEstStockBasee());
        article.setEstStockElever(command.getEstStockElever());
        article.setEstActif(command.getEstActif());
        
        Article updatedArticle = repositoryService.save(article);
        
        return new UpdateArticleCommandResponse(
            updatedArticle.getId(),
            updatedArticle.getSku(),
            updatedArticle.getCodebare(),
            updatedArticle.getNom(),
            updatedArticle.getDescription(),
            updatedArticle.getCategorie() != null ? updatedArticle.getCategorie().getId() : null,
            updatedArticle.getImageUrl(),
            updatedArticle.getUnitesDeMesure(),
            updatedArticle.getPrixAchat(),
            updatedArticle.getPrixVente(),
            updatedArticle.getTauxTaxe(),
            updatedArticle.getMarque() != null ? updatedArticle.getMarque().getId() : null,
            updatedArticle.getPrixVenteHT(),
            updatedArticle.getStockMinimum(),
            updatedArticle.getStockMaximum(),
            null,
            updatedArticle.getEstStockBasee(),
            updatedArticle.getEstStockElever(),
            updatedArticle.getEstActif(),
            updatedArticle.getCreatedAt(),
            updatedArticle.getUpdatedAt()
        );
    }
}
