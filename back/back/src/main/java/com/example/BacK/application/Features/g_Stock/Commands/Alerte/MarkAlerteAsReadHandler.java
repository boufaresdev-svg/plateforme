package com.example.BacK.application.Features.g_Stock.Commands.Alerte;

import com.example.BacK.application.interfaces.g_Stock.alerte.IAlerteStockRepositoryService;
import com.example.BacK.domain.g_Stock.AlerteStock;
import com.example.BacK.application.mediator.RequestHandler;
import org.springframework.stereotype.Component;

@Component
public class MarkAlerteAsReadHandler implements RequestHandler<MarkAlerteAsReadCommand, MarkAlerteAsReadResponse> {
    
    private final IAlerteStockRepositoryService alerteService;
    
    public MarkAlerteAsReadHandler(IAlerteStockRepositoryService alerteService) {
        this.alerteService = alerteService;
    }
    
    @Override
    public MarkAlerteAsReadResponse handle(MarkAlerteAsReadCommand request) {
        AlerteStock alerte = alerteService.getById(request.getAlerteId());
        
        if (alerte == null) {
            return new MarkAlerteAsReadResponse(request.getAlerteId(), false);
        }
        
        alerte.setIsRead(true);
        alerteService.update(alerte);
        
        return new MarkAlerteAsReadResponse(alerte.getId(), true);
    }
}
