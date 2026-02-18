package com.example.BacK.application.g_Stock.Command.stock.addStock;

import com.example.BacK.application.interfaces.g_Stock.article.IArticleRepositoryService;
import com.example.BacK.application.interfaces.g_Stock.entrepot.IEntrepotRepositoryService;
import com.example.BacK.application.interfaces.g_Stock.stock.IStockRepositoryService;
import com.example.BacK.application.interfaces.g_Stock.stockLot.IStockLotRepositoryService;
import com.example.BacK.application.interfaces.g_Fournisseur.fournisseur.IFournisseurRepositoryService;
import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Stock.Stock;
import com.example.BacK.domain.g_Stock.StockLot;
import com.example.BacK.domain.g_Stock.Article;
import com.example.BacK.domain.g_Stock.Entrepot;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component("AddStockHandler")
public class AddStockHandler implements RequestHandler<AddStockCommand, AddStockResponse> {

    private final IStockRepositoryService stockRepositoryService;
    private final IArticleRepositoryService articleRepositoryService;
    private final IEntrepotRepositoryService entrepotRepositoryService;
    private final IFournisseurRepositoryService fournisseurRepositoryService;
    private final IStockLotRepositoryService stockLotRepositoryService;
    private final AddStockValidator validator;
    private final ModelMapper modelMapper;

    public AddStockHandler(IStockRepositoryService stockRepositoryService,
                          IArticleRepositoryService articleRepositoryService,
                          IEntrepotRepositoryService entrepotRepositoryService,
                          IFournisseurRepositoryService fournisseurRepositoryService,
                          IStockLotRepositoryService stockLotRepositoryService,
                          AddStockValidator validator,
                          ModelMapper modelMapper) {
        this.stockRepositoryService = stockRepositoryService;
        this.articleRepositoryService = articleRepositoryService;
        this.entrepotRepositoryService = entrepotRepositoryService;
        this.fournisseurRepositoryService = fournisseurRepositoryService;
        this.stockLotRepositoryService = stockLotRepositoryService;
        this.validator = validator;
        this.modelMapper = modelMapper;
    }

    @Override
    public AddStockResponse handle(AddStockCommand command) {
        // Validate command
        validator.validate(command);

        // Get article and entrepot
        Article article = articleRepositoryService.findById(command.getArticleId()).get();
        Entrepot entrepot = entrepotRepositoryService.findById(command.getEntrepotId()).get();

        // Create new stock entity
        Stock stock = new Stock();
        stock.setArticle(article);
        stock.setEntrepot(entrepot);
        
        // Set fournisseur if provided
        if (command.getFournisseurId() != null && !command.getFournisseurId().isEmpty()) {
            stock.setFournisseur(fournisseurRepositoryService.getById(command.getFournisseurId()));
        }
        
        stock.setQuantite(command.getQuantite());
        stock.setDateDexpiration(command.getDateDexpiration());
        stock.setCreatedBy(command.getCreatedBy());

        // Save stock
        Stock savedStock = stockRepositoryService.add(stock);

        // Create stock lot if quantity > 0 and pricing info is provided
        if (command.getQuantite() != null && command.getQuantite() > 0 
            && command.getPrixAchatUnitaire() != null && command.getPrixVenteUnitaire() != null) {
            createStockLot(command, article, entrepot, command.getCreatedBy());
        }

        // Map to response
        AddStockResponse response = modelMapper.map(savedStock, AddStockResponse.class);
        response.setArticleId(savedStock.getArticle().getId());
        response.setEntrepotId(savedStock.getEntrepot().getId());
        if (savedStock.getFournisseur() != null) {
            response.setFournisseurId(savedStock.getFournisseur().getId());
        }
        
        return response;
    }

    private StockLot createStockLot(AddStockCommand command, Article article, Entrepot entrepot, String userId) {
        StockLot stockLot = new StockLot();
        stockLot.setArticle(article);
        stockLot.setEntrepot(entrepot);
        
        // NumeroLot will be auto-generated in @PrePersist
        
        // Set quantities
        stockLot.setQuantiteInitiale(command.getQuantite());
        stockLot.setQuantiteActuelle(command.getQuantite());
        stockLot.setQuantiteReservee(0);
        
        // Set dates
        stockLot.setDateAchat(command.getDateAchat() != null ? command.getDateAchat() : LocalDate.now());
        stockLot.setDateExpiration(command.getDateExpiration());
        
        // Set pricing
        stockLot.setPrixAchatUnitaire(command.getPrixAchatUnitaire());
        stockLot.setPrixVenteUnitaire(command.getPrixVenteUnitaire());
        
        // Set invoice information
        stockLot.setFactureUrl(command.getFactureUrl());
        stockLot.setNumeroFacture(command.getNumeroFacture());
        
        // Set notes and status
        stockLot.setEstActif(true);
        stockLot.setCreatedBy(userId);
        stockLot.setUpdatedBy(userId);
        
        // Save and return lot
        return stockLotRepositoryService.add(stockLot);
    }
}