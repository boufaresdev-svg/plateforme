package com.example.BacK.application.g_Stock.Command.ajustementStock.delete;

import com.example.BacK.application.interfaces.g_Stock.ajustementStock.IAjustementStockRepositoryService;
import com.example.BacK.application.interfaces.g_Stock.stock.IStockRepositoryService;
import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Stock.AjustementStock;
import com.example.BacK.domain.g_Stock.Stock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("DeleteAjustementStockHandler")
@RequiredArgsConstructor
public class DeleteAjustementStockHandler implements RequestHandler<DeleteAjustementStockCommand, DeleteAjustementStockResponse> {
    
    private final IAjustementStockRepositoryService ajustementStockRepositoryService;
    private final IStockRepositoryService stockRepositoryService;
    
    @Override
    public DeleteAjustementStockResponse handle(DeleteAjustementStockCommand command) {
        // Get the adjustment before deleting to restore the stock
        AjustementStock ajustement = ajustementStockRepositoryService.getById(command.getId());
        
        if (ajustement == null) {
            throw new IllegalArgumentException("Ajustement non trouvé avec l'ID: " + command.getId());
        }
        
        // Restore stock quantity to the original value (quantiteAvant)
        if (ajustement.getEntrepot() != null && ajustement.getArticle() != null) {
            Stock stock = stockRepositoryService.getByArticleIdAndEntrepotId(
                ajustement.getArticle().getId(), 
                ajustement.getEntrepot().getId()
            );
            
            if (stock != null) {
                // Restore to the original quantity before adjustment
                stock.setQuantite(ajustement.getQuantiteAvant());
                stockRepositoryService.update(stock);
            }
        }
        
        // Delete the adjustment
        ajustementStockRepositoryService.delete(command.getId());
        
        return new DeleteAjustementStockResponse("Ajustement de stock supprimé avec succès et quantité restaurée");
    }
}
