package com.example.BacK.application.g_Stock.Command.ajustementStock.update;

import com.example.BacK.application.interfaces.g_Stock.ajustementStock.IAjustementStockRepositoryService;
import com.example.BacK.application.interfaces.g_Stock.article.IArticleRepositoryService;
import com.example.BacK.application.interfaces.g_Stock.entrepot.IEntrepotRepositoryService;
import com.example.BacK.application.interfaces.g_Stock.stock.IStockRepositoryService;
import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Stock.AjustementStock;
import com.example.BacK.domain.g_Stock.Article;
import com.example.BacK.domain.g_Stock.Entrepot;
import com.example.BacK.domain.g_Stock.Stock;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component("UpdateAjustementStockHandler")
@RequiredArgsConstructor
public class UpdateAjustementStockHandler implements RequestHandler<UpdateAjustementStockCommand, UpdateAjustementStockResponse> {
    
    private final IAjustementStockRepositoryService ajustementStockRepositoryService;
    private final IArticleRepositoryService articleRepositoryService;
    private final IEntrepotRepositoryService entrepotRepositoryService;
    private final IStockRepositoryService stockRepositoryService;
    
    @Override
    public UpdateAjustementStockResponse handle(UpdateAjustementStockCommand command) {
        // Fetch existing AjustementStock
        AjustementStock ajustementStock = ajustementStockRepositoryService.getById(command.getId());
        
        // Fetch Article
        Article article = articleRepositoryService.findById(command.getArticleId())
            .orElseThrow(() -> new IllegalArgumentException("Article non trouvé avec l'ID: " + command.getArticleId()));
        
        // Fetch Entrepot if provided
        Entrepot entrepot = null;
        if (command.getEntrepotId() != null && !command.getEntrepotId().isEmpty()) {
            entrepot = entrepotRepositoryService.findById(command.getEntrepotId())
                .orElseThrow(() -> new IllegalArgumentException("Entrepôt non trouvé avec l'ID: " + command.getEntrepotId()));
        }
        
        // Get current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new IllegalArgumentException("Aucun utilisateur authentifié");
        }
        
        // Get User object from principal (cast directly since we know it's a User from Spring Security)
        com.example.BacK.domain.g_Utilisateurs.User user = (com.example.BacK.domain.g_Utilisateurs.User) authentication.getPrincipal();
        
        // Update AjustementStock
        ajustementStock.setArticle(article);
        ajustementStock.setEntrepot(entrepot);
        ajustementStock.setQuantiteAvant(command.getQuantiteAvant());
        ajustementStock.setQuantiteApres(command.getQuantiteApres());
        ajustementStock.setDateAjustement(command.getDateAjustement() != null ? command.getDateAjustement() : LocalDate.now());
        ajustementStock.setUtilisateur(user);
        ajustementStock.setRaison(command.getRaison());
        ajustementStock.setNotes(command.getNotes());
        
        // Save
        ajustementStockRepositoryService.update(ajustementStock);
        
        // Update stock quantity if entrepot is provided
        if (entrepot != null) {
            Stock stock = stockRepositoryService.getByArticleIdAndEntrepotId(article.getId(), entrepot.getId());
            if (stock != null) {
                // Update existing stock
                stock.setQuantite(command.getQuantiteApres());
                stockRepositoryService.update(stock);
            } else {
                // Create new stock entry if it doesn't exist
                Stock newStock = new Stock();
                newStock.setArticle(article);
                newStock.setEntrepot(entrepot);
                newStock.setQuantite(command.getQuantiteApres());
                stockRepositoryService.add(newStock);
            }
        }
        
        return new UpdateAjustementStockResponse("Ajustement de stock mis à jour avec succès");
    }
}
