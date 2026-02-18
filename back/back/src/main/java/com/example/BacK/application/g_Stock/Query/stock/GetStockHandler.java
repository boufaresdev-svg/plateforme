package com.example.BacK.application.g_Stock.Query.stock;

import com.example.BacK.application.interfaces.g_Stock.stock.IStockRepositoryService;
import com.example.BacK.application.interfaces.g_Stock.stockLot.IStockLotRepositoryService;
import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Stock.Stock;
import com.example.BacK.domain.g_Stock.StockLot;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component("GetStockHandler")
public class GetStockHandler implements RequestHandler<GetStockQuery, List<GetStockResponse>> {

    private final IStockRepositoryService stockRepositoryService;
    private final IStockLotRepositoryService stockLotRepositoryService;

    public GetStockHandler(IStockRepositoryService stockRepositoryService,
                          IStockLotRepositoryService stockLotRepositoryService) {
        this.stockRepositoryService = stockRepositoryService;
        this.stockLotRepositoryService = stockLotRepositoryService;
    }

    @Override
    public List<GetStockResponse> handle(GetStockQuery query) {
        List<Stock> stocks;
        
        if (query.getId() != null) {
            // Get single stock by ID
            Stock stock = stockRepositoryService.getById(query.getId());
            stocks = stock != null ? List.of(stock) : List.of();
        } else if (query.getArticleId() != null && query.getEntrepotId() != null) {
            // Get stock by article and entrepot
            Stock stock = stockRepositoryService.getByArticleIdAndEntrepotId(
                query.getArticleId(), query.getEntrepotId());
            stocks = stock != null ? List.of(stock) : List.of();
        } else if (query.getArticleId() != null) {
            // Get stocks by article
            stocks = stockRepositoryService.getByArticleId(query.getArticleId());
        } else if (query.getEntrepotId() != null) {
            // Get stocks by entrepot
            stocks = stockRepositoryService.getByEntrepotId(query.getEntrepotId());
        } else {
            // Get all stocks
            stocks = stockRepositoryService.getAll();
        }
        
        // Apply additional filters
        return stocks.stream()
                .filter(stock -> query.getCategorieId() == null || 
                    (stock.getArticle() != null && stock.getArticle().getCategorie() != null && 
                     stock.getArticle().getCategorie().getId().equals(query.getCategorieId())))
                .filter(stock -> query.getMarqueId() == null || 
                    (stock.getArticle() != null && stock.getArticle().getMarque() != null && 
                     stock.getArticle().getMarque().getId().equals(query.getMarqueId())))
                .filter(stock -> query.getFournisseurId() == null || 
                    (stock.getFournisseur() != null && 
                     stock.getFournisseur().getId().equals(query.getFournisseurId())))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    private GetStockResponse mapToResponse(Stock stock) {
        GetStockResponse response = new GetStockResponse();
        
        // Stock basic information
        response.setId(stock.getId());
        
        // Get lots for this stock to calculate quantity and pricing
        List<StockLot> lots = getLotesForStock(stock);
        
        // Calculate quantity from lots instead of using stock.quantite
        Integer calculatedQuantity = lots.stream()
            .mapToInt(lot -> lot.getQuantiteActuelle() != null ? lot.getQuantiteActuelle() : 0)
            .sum();
        response.setQuantite(calculatedQuantity);
        
        // Calculate lot-based pricing
        calculateAndSetLotPricing(response, lots, calculatedQuantity);
        
        response.setDateDexpiration(stock.getDateDexpiration());
        response.setCreatedBy(stock.getCreatedBy());
        response.setUpdatedBy(stock.getUpdatedBy());
        response.setCreatedAt(stock.getCreatedAt());
        response.setUpdatedAt(stock.getUpdatedAt());
        
        // Article information (using eager loading)
        if (stock.getArticle() != null) {
            response.setArticleId(stock.getArticle().getId());
            response.setArticleNom(stock.getArticle().getNom());
            response.setArticleDescription(stock.getArticle().getDescription());
            response.setArticleCodebare(stock.getArticle().getCodebare());
            response.setArticlePrixVente(stock.getArticle().getPrixVente());
            response.setArticlePrixAchat(stock.getArticle().getPrixAchat());
            response.setArticleStockMinimum(stock.getArticle().getStockMinimum());
            response.setArticleStockMaximum(stock.getArticle().getStockMaximum());
            
            // Category information
            if (stock.getArticle().getCategorie() != null) {
                response.setCategorieId(stock.getArticle().getCategorie().getId());
                response.setCategorieNom(stock.getArticle().getCategorie().getNom());
            }
            
            // Brand information
            if (stock.getArticle().getMarque() != null) {
                response.setMarqueId(stock.getArticle().getMarque().getId());
                response.setMarqueNom(stock.getArticle().getMarque().getNom());
            }
        }
        
        // Warehouse information (using eager loading)
        if (stock.getEntrepot() != null) {
            response.setEntrepotId(stock.getEntrepot().getId());
            response.setEntrepotNom(stock.getEntrepot().getNom());
            response.setEntrepotAdresse(stock.getEntrepot().getAdresse());
        }
        
        // Fournisseur information (using eager loading)
        if (stock.getFournisseur() != null) {
            response.setFournisseurId(stock.getFournisseur().getId());
            response.setFournisseurNom(stock.getFournisseur().getNom());
        }
        
        return response;
    }
    
    private List<StockLot> getLotesForStock(Stock stock) {
        if (stock.getArticle() == null || stock.getEntrepot() == null) {
            return List.of();
        }
        return stockLotRepositoryService.getByArticleIdAndEntrepotId(
            stock.getArticle().getId(), 
            stock.getEntrepot().getId()
        );
    }
    
    private void calculateAndSetLotPricing(GetStockResponse response, List<StockLot> lots, Integer totalQuantity) {
        response.setNombreLots(lots.size());
        
        if (lots.isEmpty() || totalQuantity == 0) {
            response.setPrixUnitaireMoyenAchat(0.0);
            response.setPrixUnitaireMoyenVente(0.0);
            response.setValeurTotaleStock(0.0);
            return;
        }
        
        // Calculate weighted average purchase price
        double totalPurchaseValue = lots.stream()
            .mapToDouble(lot -> {
                int qty = lot.getQuantiteActuelle() != null ? lot.getQuantiteActuelle() : 0;
                double price = lot.getPrixAchatUnitaire() != null ? lot.getPrixAchatUnitaire() : 0.0;
                return qty * price;
            })
            .sum();
        
        // Calculate weighted average selling price
        double totalSaleValue = lots.stream()
            .mapToDouble(lot -> {
                int qty = lot.getQuantiteActuelle() != null ? lot.getQuantiteActuelle() : 0;
                double price = lot.getPrixVenteUnitaire() != null ? lot.getPrixVenteUnitaire() : 0.0;
                return qty * price;
            })
            .sum();
        
        response.setPrixUnitaireMoyenAchat(totalPurchaseValue / totalQuantity);
        response.setPrixUnitaireMoyenVente(totalSaleValue / totalQuantity);
        response.setValeurTotaleStock(totalPurchaseValue);
    }
    
    private Integer calculateQuantityFromLots(Stock stock) {
        // Calculate total quantity from all lots for this stock
        if (stock.getArticle() == null || stock.getEntrepot() == null) {
            return 0;
        }
        
        List<StockLot> lots = stockLotRepositoryService.getByArticleIdAndEntrepotId(
            stock.getArticle().getId(), 
            stock.getEntrepot().getId()
        );
        
        return lots.stream()
            .mapToInt(lot -> lot.getQuantiteActuelle() != null ? lot.getQuantiteActuelle() : 0)
            .sum();
    }
}