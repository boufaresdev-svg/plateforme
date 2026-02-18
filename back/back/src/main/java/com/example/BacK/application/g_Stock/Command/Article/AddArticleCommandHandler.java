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
public class AddArticleCommandHandler implements RequestHandler<AddArticleCommand, AddArticleCommandResponse> {
    
    private final IArticleRepositoryService repositoryService;
    private final ICategoryRepositoryService categoryRepositoryService;
    private final IMarqueRepositoryService marqueRepositoryService;
    
    @Override
    public AddArticleCommandResponse handle(AddArticleCommand command) {
        Article article = new Article();
        // SKU will be generated automatically in @PrePersist
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
        }
        
        article.setPrixVenteHT(command.getPrixVenteHT());
        article.setStockMinimum(command.getStockMinimum());
        article.setStockMaximum(command.getStockMaximum());
        
        article.setEstStockBasee(command.getEstStockBasee() != null ? command.getEstStockBasee() : false);
        article.setEstStockElever(command.getEstStockElever() != null ? command.getEstStockElever() : false);
        article.setEstActif(command.getEstActif() != null ? command.getEstActif() : true);
        
        Article savedArticle = repositoryService.save(article);
        
        return new AddArticleCommandResponse(
            savedArticle.getId(),
            savedArticle.getSku(),
            savedArticle.getCodebare(),
            savedArticle.getNom(),
            savedArticle.getDescription(),
            savedArticle.getCategorie() != null ? savedArticle.getCategorie().getId() : null,
            savedArticle.getImageUrl(),
            savedArticle.getUnitesDeMesure(),
            savedArticle.getPrixAchat(),
            savedArticle.getPrixVente(),
            savedArticle.getTauxTaxe(),
            savedArticle.getMarque() != null ? savedArticle.getMarque().getId() : null,
            savedArticle.getPrixVenteHT(),
            savedArticle.getStockMinimum(),
            savedArticle.getStockMaximum(),
            null,
            savedArticle.getEstStockBasee(),
            savedArticle.getEstStockElever(),
            savedArticle.getEstActif(),
            savedArticle.getCreatedAt(),
            savedArticle.getUpdatedAt()
        );
    }
}
