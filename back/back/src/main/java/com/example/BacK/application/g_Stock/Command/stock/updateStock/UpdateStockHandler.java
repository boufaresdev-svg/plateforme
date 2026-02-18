package com.example.BacK.application.g_Stock.Command.stock.updateStock;

import com.example.BacK.application.interfaces.g_Stock.stock.IStockRepositoryService;
import com.example.BacK.application.interfaces.g_Fournisseur.fournisseur.IFournisseurRepositoryService;
import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Stock.Stock;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component("UpdateStockHandler")
public class UpdateStockHandler implements RequestHandler<UpdateStockCommand, UpdateStockResponse> {

    private final IStockRepositoryService stockRepositoryService;
    private final IFournisseurRepositoryService fournisseurRepositoryService;
    private final UpdateStockValidator validator;
    private final ModelMapper modelMapper;

    public UpdateStockHandler(IStockRepositoryService stockRepositoryService,
                             IFournisseurRepositoryService fournisseurRepositoryService,
                             UpdateStockValidator validator,
                             ModelMapper modelMapper) {
        this.stockRepositoryService = stockRepositoryService;
        this.fournisseurRepositoryService = fournisseurRepositoryService;
        this.validator = validator;
        this.modelMapper = modelMapper;
    }

    @Override
    public UpdateStockResponse handle(UpdateStockCommand command) {
        // Validate command
        validator.validate(command);

        // Get existing stock
        Stock existingStock = stockRepositoryService.getById(command.getId());
        
        // Update fields if provided
        if (command.getQuantite() != null) {
            existingStock.setQuantite(command.getQuantite());
        }
        if (command.getDateDexpiration() != null) {
            existingStock.setDateDexpiration(command.getDateDexpiration());
        }
        if (command.getFournisseurId() != null) {
            if (command.getFournisseurId().isEmpty()) {
                existingStock.setFournisseur(null);
            } else {
                existingStock.setFournisseur(fournisseurRepositoryService.getById(command.getFournisseurId()));
            }
        }
        if (command.getUpdatedBy() != null) {
            existingStock.setUpdatedBy(command.getUpdatedBy());
        }

        // Save updated stock
        Stock updatedStock = stockRepositoryService.update(existingStock);

        // Map to response
        UpdateStockResponse response = modelMapper.map(updatedStock, UpdateStockResponse.class);
        response.setArticleId(updatedStock.getArticle().getId());
        response.setEntrepotId(updatedStock.getEntrepot().getId());
        if (updatedStock.getFournisseur() != null) {
            response.setFournisseurId(updatedStock.getFournisseur().getId());
        }
        
        return response;
    }
}