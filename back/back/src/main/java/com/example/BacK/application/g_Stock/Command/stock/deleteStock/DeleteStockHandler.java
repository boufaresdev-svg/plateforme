package com.example.BacK.application.g_Stock.Command.stock.deleteStock;

import com.example.BacK.application.interfaces.g_Stock.stock.IStockRepositoryService;
import com.example.BacK.application.mediator.RequestHandler;
import org.springframework.stereotype.Component;

@Component("DeleteStockHandler")
public class DeleteStockHandler implements RequestHandler<DeleteStockCommand, DeleteStockResponse> {

    private final IStockRepositoryService stockRepositoryService;
    private final DeleteStockValidator validator;

    public DeleteStockHandler(IStockRepositoryService stockRepositoryService,
                             DeleteStockValidator validator) {
        this.stockRepositoryService = stockRepositoryService;
        this.validator = validator;
    }

    @Override
    public DeleteStockResponse handle(DeleteStockCommand command) {
        try {
            // Validate command
            validator.validate(command);

            // Delete stock
            stockRepositoryService.delete(command.getId());

            return new DeleteStockResponse(true);
            
        } catch (Exception e) {
            return new DeleteStockResponse(false, e.getMessage());
        }
    }
}