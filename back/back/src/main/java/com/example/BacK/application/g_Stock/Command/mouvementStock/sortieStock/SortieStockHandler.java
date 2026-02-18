package com.example.BacK.application.g_Stock.Command.mouvementStock.sortieStock;

import com.example.BacK.application.interfaces.g_Stock.article.IArticleRepositoryService;
import com.example.BacK.application.interfaces.g_Stock.entrepot.IEntrepotRepositoryService;
import com.example.BacK.application.interfaces.g_Stock.mouvementStock.IMouvementStockRepositoryService;
import com.example.BacK.application.interfaces.g_Stock.stock.IStockRepositoryService;
import com.example.BacK.application.interfaces.g_Stock.stockLot.IStockLotRepositoryService;
import com.example.BacK.application.interfaces.g_Stock.stockLotMouvement.IStockLotMouvementRepositoryService;
import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Stock.Article;
import com.example.BacK.domain.g_Stock.Entrepot;
import com.example.BacK.domain.g_Stock.MouvementStock;
import com.example.BacK.domain.g_Stock.Stock;
import com.example.BacK.domain.g_Stock.StockLot;
import com.example.BacK.domain.g_Stock.StockLotMouvement;
import com.example.BacK.domain.g_Stock.enumEntity.TypeMouvement;
import com.example.BacK.domain.g_Utilisateurs.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
public class SortieStockHandler implements RequestHandler<SortieStockCommand, SortieStockResponse> {
    
    private final IMouvementStockRepositoryService mouvementStockService;
    private final IArticleRepositoryService articleService;
    private final IEntrepotRepositoryService entrepotService;
    private final IStockRepositoryService stockService;
    private final IStockLotRepositoryService stockLotService;
    private final IStockLotMouvementRepositoryService stockLotMouvementService;
    private final SortieStockValidator validator;
    
    public SortieStockHandler(
            IMouvementStockRepositoryService mouvementStockService,
            IArticleRepositoryService articleService,
            IEntrepotRepositoryService entrepotService,
            IStockRepositoryService stockService,
            IStockLotRepositoryService stockLotService,
            IStockLotMouvementRepositoryService stockLotMouvementService,
            SortieStockValidator validator) {
        this.mouvementStockService = mouvementStockService;
        this.articleService = articleService;
        this.entrepotService = entrepotService;
        this.stockService = stockService;
        this.stockLotService = stockLotService;
        this.stockLotMouvementService = stockLotMouvementService;
        this.validator = validator;
    }
    
    @Override
    public SortieStockResponse handle(SortieStockCommand command) {
        // Validate command
        List<String> errors = validator.validate(command);
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Validation errors: " + String.join(", ", errors));
        }
        
        // Get the stock lot (batch) that user wants to take from
        StockLot stockLot = stockLotService.getById(command.getStockLotId());
        
        // Validate that the batch has enough quantity
        if (stockLot.getQuantiteDisponible() < command.getQuantite().intValue()) {
            throw new IllegalArgumentException("Quantité insuffisante dans le lot " + stockLot.getNumeroLot() + 
                ". Disponible: " + stockLot.getQuantiteDisponible() + ", Demandé: " + command.getQuantite().intValue());
        }
        
        // Get entities
        Article article = articleService.getById(command.getArticleId());
        Entrepot sourceEntrepot = entrepotService.getById(command.getSourceEntrepotId());
        
        // Get current user
        String currentUserId = getCurrentUserId();
        User currentUser = getCurrentUser();
        
        // Create movement
        MouvementStock mouvementStock = new MouvementStock();
        mouvementStock.setTypeMouvement(TypeMouvement.SORTIE);
        mouvementStock.setArticle(article);
        mouvementStock.setSourceEntrepot(sourceEntrepot);
        mouvementStock.setTypeSortie(command.getTypeSortie());
        mouvementStock.setStockLotId(command.getStockLotId()); // Link to batch
        
        // Set quantity and date
        mouvementStock.setQuantite(command.getQuantite());
        LocalDateTime dateMouvement = command.getDateMouvement() != null ? 
            command.getDateMouvement().atStartOfDay() : LocalDateTime.now();
        mouvementStock.setDateMouvement(dateMouvement);
        
        // Set user
        if (currentUser != null) {
            mouvementStock.setUtilisateur(currentUser);
        }
        
        // Set references
        mouvementStock.setReference(command.getReference());
        mouvementStock.setNumeroBonLivraison(command.getNumeroBonLivraison());
        mouvementStock.setReferenceCommandeClient(command.getReferenceCommandeClient());
        mouvementStock.setDestinataire(command.getDestinataire());
        mouvementStock.setMotif(command.getMotif());
        mouvementStock.setStatut(command.getStatut());
        mouvementStock.setNotes(command.getNotes());
        mouvementStock.setCreatedBy(currentUserId);
        mouvementStock.setUpdatedBy(currentUserId);
        
        // Save movement
        MouvementStock savedMouvement = mouvementStockService.add(mouvementStock);
        
        // Update the batch quantity
        updateBatchQuantity(stockLot, command.getQuantite().intValue(), savedMouvement, currentUserId, currentUser);
        
        // Update global stock levels (decrease quantity)
        updateStockLevels(command.getArticleId(), command.getSourceEntrepotId(), command.getQuantite().intValue());
        
        // Build response
        return buildResponse(savedMouvement, article, sourceEntrepot, currentUser);
    }
    
    private void updateBatchQuantity(StockLot stockLot, Integer quantite, MouvementStock mouvementStock, String userId, User user) {
        Integer quantiteAvant = stockLot.getQuantiteActuelle();
        Integer quantiteApres = quantiteAvant - quantite;
        
        // Update batch quantity
        stockLot.setQuantiteActuelle(quantiteApres);
        stockLot.setUpdatedBy(userId);
        stockLotService.update(stockLot);
        
        // Create batch movement record for tracking
        StockLotMouvement stockLotMouvement = new StockLotMouvement();
        stockLotMouvement.setStockLot(stockLot);
        stockLotMouvement.setMouvementStock(mouvementStock);
        stockLotMouvement.setTypeMouvement(TypeMouvement.SORTIE);
        stockLotMouvement.setQuantite(quantite);
        stockLotMouvement.setQuantiteAvant(quantiteAvant);
        stockLotMouvement.setQuantiteApres(quantiteApres);
        stockLotMouvement.setPrixUnitaire(stockLot.getPrixVenteUnitaire());
        stockLotMouvement.setValeurTotale(quantite * stockLot.getPrixVenteUnitaire());
        stockLotMouvement.setDateMouvement(mouvementStock.getDateMouvement());
        stockLotMouvement.setUtilisateur(user);
        stockLotMouvement.setReference(mouvementStock.getReference());
        stockLotMouvement.setCreatedBy(userId);
        stockLotMouvement.setUpdatedBy(userId);
        
        stockLotMouvementService.add(stockLotMouvement);
    }
    
    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            User currentUser = (User) authentication.getPrincipal();
            return currentUser.getId();
        }
        return "SYSTEM";
    }
    
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }
        return null;
    }
    
    private void updateStockLevels(String articleId, String entrepotId, Integer quantite) {
        Stock stock = stockService.findByArticleIdAndEntrepotId(articleId, entrepotId);
        
        if (stock == null) {
            throw new IllegalStateException("Stock not found for article " + articleId + " in warehouse " + entrepotId);
        }
        
        // Decrease global stock quantity (lot quantity already validated)
        // The lot system manages the actual available quantities
        stock.setQuantite(stock.getQuantite() - quantite);
        stockService.update(stock);
    }
    
    private SortieStockResponse buildResponse(MouvementStock mouvement, Article article, Entrepot sourceEntrepot, User user) {
        return SortieStockResponse.builder()
            .id(mouvement.getId())
            .typeMouvement(mouvement.getTypeMouvement())
            .articleId(article.getId())
            .articleNom(article.getNom())
            .articleSku(article.getSku())
            .sourceEntrepotId(sourceEntrepot.getId())
            .sourceEntrepotNom(sourceEntrepot.getNom())
            .quantite(mouvement.getQuantite())
            .typeSortie(mouvement.getTypeSortie())
            .reference(mouvement.getReference())
            .numeroBonLivraison(mouvement.getNumeroBonLivraison())
            .referenceCommandeClient(mouvement.getReferenceCommandeClient())
            .destinataire(mouvement.getDestinataire())
            .motif(mouvement.getMotif())
            .dateMouvement(mouvement.getDateMouvement())
            .statut(mouvement.getStatut())
            .utilisateurId(user != null ? user.getId() : null)
            .utilisateurNom(user != null ? user.getPrenom() + " " + user.getNom() : null)
            .stockLotId(mouvement.getStockLotId())
            .build();
    }
}
