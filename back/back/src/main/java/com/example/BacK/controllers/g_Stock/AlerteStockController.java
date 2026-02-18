package com.example.BacK.controllers.g_Stock;

import com.example.BacK.application.Features.g_Stock.Commands.Alerte.MarkAlerteAsReadCommand;
import com.example.BacK.application.Features.g_Stock.Commands.Alerte.MarkAlerteAsReadResponse;
import com.example.BacK.application.Features.g_Stock.Commands.Alerte.UpdateAlerteStatusCommand;
import com.example.BacK.application.Features.g_Stock.Commands.Alerte.UpdateAlerteStatusResponse;
import com.example.BacK.application.Features.g_Stock.Queries.Alerte.GetStockAlertsQuery;
import com.example.BacK.application.Features.g_Stock.Queries.Alerte.GetStockAlertsResponse;
import com.example.BacK.application.interfaces.g_Stock.alerte.IAlerteStockRepositoryService;
import com.example.BacK.domain.g_Stock.enumEntity.AlertePriorite;
import com.example.BacK.domain.g_Stock.enumEntity.AlerteStatut;
import com.example.BacK.domain.g_Stock.enumEntity.AlerteType;
import com.example.BacK.application.mediator.Mediator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stock/alertes")
public class AlerteStockController {
    
    private final Mediator mediator;
    private final IAlerteStockRepositoryService alerteService;
    
    public AlerteStockController(Mediator mediator, IAlerteStockRepositoryService alerteService) {
        this.mediator = mediator;
        this.alerteService = alerteService;
    }
    
    @GetMapping
    public ResponseEntity<GetStockAlertsResponse> getAlertes(
            @RequestParam(required = false) AlerteType type,
            @RequestParam(required = false) AlertePriorite priorite,
            @RequestParam(required = false) AlerteStatut statut,
            @RequestParam(required = false) Boolean isRead,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "15") Integer size,
            @RequestParam(required = false) String searchTerm) {
        
        GetStockAlertsQuery query = new GetStockAlertsQuery();
        query.setType(type);
        query.setPriorite(priorite);
        query.setStatut(statut);
        query.setIsRead(isRead);
        query.setPage(page);
        query.setSize(size);
        query.setSearchTerm(searchTerm);
        
        List<GetStockAlertsResponse> results = mediator.sendToHandlers(query);
        
        if (!results.isEmpty()) {
            return ResponseEntity.ok(results.get(0));
        }
        return ResponseEntity.ok(new GetStockAlertsResponse());
    }
    
    @PostMapping("/{alerteId}/read")
    public ResponseEntity<MarkAlerteAsReadResponse> markAsRead(@PathVariable String alerteId) {
        MarkAlerteAsReadCommand command = new MarkAlerteAsReadCommand(alerteId);
        List<MarkAlerteAsReadResponse> results = mediator.sendToHandlers(command);
        
        if (!results.isEmpty() && results.get(0).isSuccess()) {
            return ResponseEntity.ok(results.get(0));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                results.isEmpty() ? new MarkAlerteAsReadResponse(alerteId, false) : results.get(0)
            );
        }
    }
    
    @PutMapping("/{alerteId}/status")
    public ResponseEntity<UpdateAlerteStatusResponse> updateStatus(
            @PathVariable String alerteId,
            @RequestBody UpdateAlerteStatusCommand command) {
        
        command.setAlerteId(alerteId);
        List<UpdateAlerteStatusResponse> results = mediator.sendToHandlers(command);
        
        if (!results.isEmpty() && results.get(0).isSuccess()) {
            return ResponseEntity.ok(results.get(0));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                results.isEmpty() ? new UpdateAlerteStatusResponse(alerteId, false, "Not found") : results.get(0)
            );
        }
    }
    
    @PostMapping("/generate")
    public ResponseEntity<String> generateAlerts() {
        alerteService.generateStockAlerts();
        return ResponseEntity.ok("Alerts generated successfully");
    }
    
    @GetMapping("/statistics")
    public ResponseEntity<AlerteStatisticsResponse> getStatistics() {
        Long totalActive = alerteService.countByStatut(AlerteStatut.ACTIVE);
        Long unread = alerteService.countUnread();
        
        AlerteStatisticsResponse stats = new AlerteStatisticsResponse();
        stats.setTotalActive(totalActive);
        stats.setUnread(unread);
        
        return ResponseEntity.ok(stats);
    }
    
    // Inner class for statistics response
    @lombok.Getter
    @lombok.Setter
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    public static class AlerteStatisticsResponse {
        private Long totalActive;
        private Long unread;
    }
}
