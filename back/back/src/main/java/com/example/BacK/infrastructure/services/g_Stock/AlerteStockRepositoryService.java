package com.example.BacK.infrastructure.services.g_Stock;

import com.example.BacK.application.interfaces.g_Stock.alerte.IAlerteStockRepositoryService;
import com.example.BacK.application.interfaces.g_Stock.article.IArticleRepositoryService;
import com.example.BacK.application.interfaces.g_Stock.stock.IStockRepositoryService;
import com.example.BacK.domain.g_Stock.AlerteStock;
import com.example.BacK.domain.g_Stock.Article;
import com.example.BacK.domain.g_Stock.Stock;
import com.example.BacK.domain.g_Stock.enumEntity.AlertePriorite;
import com.example.BacK.domain.g_Stock.enumEntity.AlerteStatut;
import com.example.BacK.domain.g_Stock.enumEntity.AlerteType;
import com.example.BacK.infrastructure.g_Stock.Repositories.AlerteStockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class AlerteStockRepositoryService implements IAlerteStockRepositoryService {
    
    private final AlerteStockRepository repository;
    private final IStockRepositoryService stockService;
    private final IArticleRepositoryService articleService;
    
    public AlerteStockRepositoryService(
            AlerteStockRepository repository,
            IStockRepositoryService stockService,
            IArticleRepositoryService articleService) {
        this.repository = repository;
        this.stockService = stockService;
        this.articleService = articleService;
    }
    
    @Override
    public AlerteStock add(AlerteStock alerte) {
        return repository.save(alerte);
    }
    
    @Override
    public AlerteStock update(AlerteStock alerte) {
        return repository.save(alerte);
    }
    
    @Override
    public void delete(String id) {
        repository.deleteById(id);
    }
    
    @Override
    public AlerteStock getById(String id) {
        return repository.findById(id).orElse(null);
    }
    
    @Override
    public List<AlerteStock> getAll() {
        return repository.findAll();
    }
    
    @Override
    public List<AlerteStock> getByStatut(AlerteStatut statut) {
        return repository.findByStatut(statut);
    }
    
    @Override
    public List<AlerteStock> getByType(AlerteType type) {
        return repository.findByType(type);
    }
    
    @Override
    public List<AlerteStock> getByPriorite(AlertePriorite priorite) {
        return repository.findByPriorite(priorite);
    }
    
    @Override
    public List<AlerteStock> getUnread() {
        return repository.findByIsReadFalse();
    }
    
    @Override
    public List<AlerteStock> getActive() {
        return repository.findActiveAlerts(AlerteStatut.ACTIVE);
    }
    
    @Override
    public List<AlerteStock> searchAlerts(AlerteType type, AlertePriorite priorite, AlerteStatut statut, Boolean isRead) {
        return repository.searchAlerts(type, priorite, statut, isRead);
    }
    
    @Override
    public Long countByStatut(AlerteStatut statut) {
        return repository.countByStatut(statut);
    }
    
    @Override
    public Long countUnread() {
        return repository.countUnread();
    }
    
    @Override
    public void generateStockAlerts() {
        List<Stock> allStocks = stockService.getAll();
        
        for (Stock stock : allStocks) {
            Article article = stock.getArticle();
            
            if (article.getStockMinimum() == null) {
                continue; // Skip if no minimum threshold set
            }
            
            Integer currentQuantity = stock.getQuantite();
            Integer stockMin = article.getStockMinimum();
            Integer stockMax = article.getStockMaximum();
            
            // Check for existing active alert for this stock
            List<AlerteStock> existingAlerts = repository.findByArticleId(article.getId())
                .stream()
                .filter(a -> a.getStatut() == AlerteStatut.ACTIVE && 
                           a.getEntrepot() != null && 
                           a.getEntrepot().getId().equals(stock.getEntrepot().getId()))
                .toList();
            
            // Rupture de stock (out of stock)
            if (currentQuantity == 0) {
                if (existingAlerts.stream().noneMatch(a -> a.getType() == AlerteType.RUPTURE)) {
                    createAlert(
                        AlerteType.RUPTURE,
                        AlertePriorite.CRITICAL,
                        "Rupture de Stock",
                        String.format("L'article '%s' est en rupture de stock", article.getNom()),
                        article,
                        stock,
                        currentQuantity,
                        stockMin
                    );
                }
            }
            // Stock faible (low stock)
            else if (currentQuantity <= stockMin) {
                if (existingAlerts.stream().noneMatch(a -> a.getType() == AlerteType.STOCK_FAIBLE)) {
                    createAlert(
                        AlerteType.STOCK_FAIBLE,
                        AlertePriorite.HIGH,
                        "Stock Faible",
                        String.format("L'article '%s' a atteint le seuil minimum (%d unités restantes)", 
                                    article.getNom(), currentQuantity),
                        article,
                        stock,
                        currentQuantity,
                        stockMin
                    );
                }
            }
            // Stock élevé (high stock)
            else if (stockMax != null && currentQuantity >= stockMax) {
                if (existingAlerts.stream().noneMatch(a -> a.getType() == AlerteType.STOCK_ELEVE)) {
                    createAlert(
                        AlerteType.STOCK_ELEVE,
                        AlertePriorite.LOW,
                        "Stock Élevé",
                        String.format("L'article '%s' a dépassé le seuil maximum (%d unités)", 
                                    article.getNom(), currentQuantity),
                        article,
                        stock,
                        currentQuantity,
                        stockMax
                    );
                }
            }
            // Resolve alerts if stock is back to normal
            else {
                existingAlerts.forEach(alert -> {
                    if (alert.getType() == AlerteType.RUPTURE || 
                        alert.getType() == AlerteType.STOCK_FAIBLE || 
                        alert.getType() == AlerteType.STOCK_ELEVE) {
                        alert.setStatut(AlerteStatut.RESOLVED);
                        alert.setDateModification(LocalDateTime.now());
                        repository.save(alert);
                    }
                });
            }
        }
    }
    
    private void createAlert(AlerteType type, AlertePriorite priorite, String titre, 
                           String message, Article article, Stock stock, 
                           Integer quantiteActuelle, Integer seuil) {
        AlerteStock alerte = new AlerteStock();
        alerte.setType(type);
        alerte.setPriorite(priorite);
        alerte.setStatut(AlerteStatut.ACTIVE);
        alerte.setTitre(titre);
        alerte.setMessage(message);
        alerte.setDescription(String.format("Entrepôt: %s | SKU: %s | Quantité actuelle: %d | Seuil: %d",
                                           stock.getEntrepot().getNom(),
                                           article.getSku(),
                                           quantiteActuelle,
                                           seuil));
        alerte.setArticle(article);
        alerte.setEntrepot(stock.getEntrepot());
        alerte.setQuantiteActuelle(quantiteActuelle);
        alerte.setSeuilMinimum(article.getStockMinimum());
        alerte.setSeuilCritique(0);
        alerte.setDateCreation(LocalDateTime.now());
        alerte.setIsRead(false);
        alerte.setIsArchived(false);
        
        repository.save(alerte);
    }
}
