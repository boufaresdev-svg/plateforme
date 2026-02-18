package com.example.BacK.application.g_Stock.Command.mouvementStock.transfertStock;

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
public class TransfertStockHandler implements RequestHandler<TransfertStockCommand, TransfertStockResponse> {
    
    private final IMouvementStockRepositoryService mouvementStockService;
    private final IArticleRepositoryService articleService;
    private final IEntrepotRepositoryService entrepotService;
    private final IStockRepositoryService stockService;
    private final IStockLotRepositoryService stockLotService;
    private final IStockLotMouvementRepositoryService stockLotMouvementService;
    private final TransfertStockValidator validator;
    
    public TransfertStockHandler(
            IMouvementStockRepositoryService mouvementStockService,
            IArticleRepositoryService articleService,
            IEntrepotRepositoryService entrepotService,
            IStockRepositoryService stockService,
            IStockLotRepositoryService stockLotService,
            IStockLotMouvementRepositoryService stockLotMouvementService,
            TransfertStockValidator validator) {
        this.mouvementStockService = mouvementStockService;
        this.articleService = articleService;
        this.entrepotService = entrepotService;
        this.stockService = stockService;
        this.stockLotService = stockLotService;
        this.stockLotMouvementService = stockLotMouvementService;
        this.validator = validator;
    }
    
    @Override
    public TransfertStockResponse handle(TransfertStockCommand command) {
        // Validate command
        List<String> errors = validator.validate(command);
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Validation errors: " + String.join(", ", errors));
        }
        
        // Get the stock lot (batch) that user wants to transfer from
        StockLot sourceLot = stockLotService.getById(command.getStockLotId());
        
        // Validate that the batch has enough quantity
        if (sourceLot.getQuantiteDisponible() < command.getQuantite().intValue()) {
            throw new IllegalArgumentException("Quantité insuffisante dans le lot " + sourceLot.getNumeroLot() + 
                ". Disponible: " + sourceLot.getQuantiteDisponible() + ", Demandé: " + command.getQuantite().intValue());
        }
        
        // Get entities
        Article article = articleService.getById(command.getArticleId());
        Entrepot sourceEntrepot = entrepotService.getById(command.getSourceEntrepotId());
        Entrepot destinationEntrepot = entrepotService.getById(command.getDestinationEntrepotId());
        
        // Get current user
        String currentUserId = getCurrentUserId();
        User currentUser = getCurrentUser();
        
        // Create movement
        MouvementStock mouvementStock = new MouvementStock();
        mouvementStock.setTypeMouvement(TypeMouvement.TRANSFERT);
        mouvementStock.setArticle(article);
        mouvementStock.setSourceEntrepot(sourceEntrepot);
        mouvementStock.setDestinationEntrepot(destinationEntrepot);
        mouvementStock.setStockLotId(command.getStockLotId()); // Link to source batch
        
        // Set quantity and date
        mouvementStock.setQuantite(command.getQuantite());
        LocalDateTime dateMouvement = command.getDateMouvement() != null ? 
            command.getDateMouvement().atStartOfDay() : LocalDateTime.now();
        mouvementStock.setDateMouvement(dateMouvement);
        
        // Set user
        if (currentUser != null) {
            mouvementStock.setUtilisateur(currentUser);
        }
        mouvementStock.setCreatedBy(currentUserId);
        mouvementStock.setUpdatedBy(currentUserId);
        
        // Set references
        mouvementStock.setReference(command.getReference());
        mouvementStock.setMotif(command.getMotif());
        
        // Set status
        mouvementStock.setStatut(command.getStatut());
        
        // Set notes
        mouvementStock.setNotes(command.getNotes());
        
        // Save movement
        MouvementStock savedMouvement = mouvementStockService.add(mouvementStock);
        
        // Update lot quantities and create lot tracking records
        updateLotQuantities(sourceLot, command.getQuantite().intValue(), savedMouvement, 
            destinationEntrepot, currentUserId, currentUser);
        
        // Update global stock levels (decrease from source, increase in destination)
        updateStockLevels(savedMouvement);
        
        // Build response
        return buildResponse(savedMouvement, article, sourceEntrepot, destinationEntrepot, currentUser);
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
    
    private void updateLotQuantities(StockLot sourceLot, Integer quantite, MouvementStock mouvementStock,
                                      Entrepot destinationEntrepot, String userId, User user) {
        Integer quantiteAvant = sourceLot.getQuantiteActuelle();
        Integer quantiteApres = quantiteAvant - quantite;
        
        // Decrease quantity from source lot
        sourceLot.setQuantiteActuelle(quantiteApres);
        sourceLot.setUpdatedBy(userId);
        stockLotService.update(sourceLot);
        
        // Create source lot movement record (SORTIE from source lot)
        StockLotMouvement sourceLotMouvement = new StockLotMouvement();
        sourceLotMouvement.setStockLot(sourceLot);
        sourceLotMouvement.setMouvementStock(mouvementStock);
        sourceLotMouvement.setTypeMouvement(TypeMouvement.SORTIE);
        sourceLotMouvement.setQuantite(quantite);
        sourceLotMouvement.setQuantiteAvant(quantiteAvant);
        sourceLotMouvement.setQuantiteApres(quantiteApres);
        sourceLotMouvement.setPrixUnitaire(sourceLot.getPrixVenteUnitaire());
        sourceLotMouvement.setValeurTotale(quantite * sourceLot.getPrixVenteUnitaire());
        sourceLotMouvement.setDateMouvement(mouvementStock.getDateMouvement());
        sourceLotMouvement.setUtilisateur(user);
        sourceLotMouvement.setReference("TRANSFERT-" + mouvementStock.getReference());
        sourceLotMouvement.setCreatedBy(userId);
        sourceLotMouvement.setUpdatedBy(userId);
        stockLotMouvementService.add(sourceLotMouvement);
        
        // Create new lot in destination warehouse (same batch properties)
        StockLot destinationLot = new StockLot();
        destinationLot.setNumeroLot(sourceLot.getNumeroLot() + "-TRANSFERT");
        destinationLot.setArticle(sourceLot.getArticle());
        destinationLot.setEntrepot(destinationEntrepot);
        destinationLot.setQuantiteInitiale(quantite);
        destinationLot.setQuantiteActuelle(quantite);
        destinationLot.setQuantiteReservee(0);
        destinationLot.setPrixAchatUnitaire(sourceLot.getPrixAchatUnitaire());
        destinationLot.setPrixVenteUnitaire(sourceLot.getPrixVenteUnitaire());
        destinationLot.setDateAchat(sourceLot.getDateAchat());
        destinationLot.setDateExpiration(sourceLot.getDateExpiration());
        destinationLot.setReferenceFournisseur(sourceLot.getReferenceFournisseur());
        destinationLot.setNumeroFacture(sourceLot.getNumeroFacture());
        destinationLot.setFactureUrl(sourceLot.getFactureUrl());
        destinationLot.setEstActif(true);
        destinationLot.setCreatedBy(userId);
        destinationLot.setUpdatedBy(userId);
        StockLot savedDestLot = stockLotService.add(destinationLot);
        
        // Create destination lot movement record (ENTREE to destination lot)
        StockLotMouvement destLotMouvement = new StockLotMouvement();
        destLotMouvement.setStockLot(savedDestLot);
        destLotMouvement.setMouvementStock(mouvementStock);
        destLotMouvement.setTypeMouvement(TypeMouvement.ENTREE);
        destLotMouvement.setQuantite(quantite);
        destLotMouvement.setQuantiteAvant(0);
        destLotMouvement.setQuantiteApres(quantite);
        destLotMouvement.setPrixUnitaire(sourceLot.getPrixAchatUnitaire());
        destLotMouvement.setValeurTotale(quantite * sourceLot.getPrixAchatUnitaire());
        destLotMouvement.setDateMouvement(mouvementStock.getDateMouvement());
        destLotMouvement.setUtilisateur(user);
        destLotMouvement.setReference("TRANSFERT-" + mouvementStock.getReference());
        destLotMouvement.setCreatedBy(userId);
        destLotMouvement.setUpdatedBy(userId);
        stockLotMouvementService.add(destLotMouvement);
    }
    
    private void updateStockLevels(MouvementStock mouvement) {
        String articleId = mouvement.getArticle().getId();
        
        // Decrease from source warehouse
        Stock sourceStock = stockService.findByArticleIdAndEntrepotId(
            articleId,
            mouvement.getSourceEntrepot().getId()
        );
        
        if (sourceStock == null) {
            throw new IllegalStateException("Stock source not found");
        }
        
        sourceStock.setQuantite(sourceStock.getQuantite() - mouvement.getQuantite().intValue());
        stockService.update(sourceStock);
        
        // Increase in destination warehouse
        Stock destStock = stockService.findByArticleIdAndEntrepotId(
            articleId,
            mouvement.getDestinationEntrepot().getId()
        );
        
        if (destStock == null) {
            // Create new stock entry if doesn't exist
            destStock = new Stock();
            // Don't set ID - let @GeneratedValue handle it
            
            Article article = articleService.getById(articleId);
            destStock.setArticle(article);
            
            Entrepot entrepot = entrepotService.getById(mouvement.getDestinationEntrepot().getId());
            destStock.setEntrepot(entrepot);
            
            destStock.setQuantite(mouvement.getQuantite().intValue());
            stockService.add(destStock);
        } else {
            destStock.setQuantite(destStock.getQuantite() + mouvement.getQuantite().intValue());
            stockService.update(destStock);
        }
    }
    
    private TransfertStockResponse buildResponse(MouvementStock mouvement, Article article, 
                                                  Entrepot sourceEntrepot, Entrepot destinationEntrepot, User user) {
        return TransfertStockResponse.builder()
            .id(mouvement.getId())
            .typeMouvement(mouvement.getTypeMouvement())
            .articleId(article.getId())
            .articleNom(article.getNom())
            .articleSku(article.getSku())
            .sourceEntrepotId(sourceEntrepot.getId())
            .sourceEntrepotNom(sourceEntrepot.getNom())
            .destinationEntrepotId(destinationEntrepot.getId())
            .destinationEntrepotNom(destinationEntrepot.getNom())
            .quantite(mouvement.getQuantite())
            .reference(mouvement.getReference())
            .motif(mouvement.getMotif())
            .dateMouvement(mouvement.getDateMouvement())
            .statut(mouvement.getStatut())
            .utilisateurId(user != null ? user.getId() : null)
            .utilisateurNom(user != null ? user.getPrenom() + " " + user.getNom() : null)
            .stockLotId(mouvement.getStockLotId())
            .build();
    }
}
