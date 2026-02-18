package com.example.BacK.application.Features.g_Stock.Commands.Alerte;

import com.example.BacK.application.interfaces.g_Stock.alerte.IAlerteStockRepositoryService;
import com.example.BacK.domain.g_Stock.AlerteStock;
import com.example.BacK.application.mediator.RequestHandler;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class UpdateAlerteStatusHandler implements RequestHandler<UpdateAlerteStatusCommand, UpdateAlerteStatusResponse> {
    
    private final IAlerteStockRepositoryService alerteService;
    
    public UpdateAlerteStatusHandler(IAlerteStockRepositoryService alerteService) {
        this.alerteService = alerteService;
    }
    
    @Override
    public UpdateAlerteStatusResponse handle(UpdateAlerteStatusCommand request) {
        AlerteStock alerte = alerteService.getById(request.getAlerteId());
        
        if (alerte == null) {
            return new UpdateAlerteStatusResponse(
                request.getAlerteId(),
                false,
                "Alerte not found"
            );
        }
        
        alerte.setStatut(request.getStatut());
        alerte.setDateModification(LocalDateTime.now());
        
        alerteService.update(alerte);
        
        return new UpdateAlerteStatusResponse(
            alerte.getId(),
            true,
            "Alerte status updated successfully"
        );
    }
}
