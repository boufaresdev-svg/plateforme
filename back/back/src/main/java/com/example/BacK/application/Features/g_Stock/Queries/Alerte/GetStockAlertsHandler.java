package com.example.BacK.application.Features.g_Stock.Queries.Alerte;

import com.example.BacK.application.interfaces.g_Stock.alerte.IAlerteStockRepositoryService;
import com.example.BacK.domain.g_Stock.AlerteStock;
import com.example.BacK.domain.g_Stock.enumEntity.AlertePriorite;
import com.example.BacK.domain.g_Stock.enumEntity.AlerteStatut;
import com.example.BacK.application.mediator.RequestHandler;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class GetStockAlertsHandler implements RequestHandler<GetStockAlertsQuery, GetStockAlertsResponse> {
    
    private final IAlerteStockRepositoryService alerteService;
    
    public GetStockAlertsHandler(IAlerteStockRepositoryService alerteService) {
        this.alerteService = alerteService;
    }
    
    @Override
    public GetStockAlertsResponse handle(GetStockAlertsQuery request) {
        // Generate fresh alerts before querying
        alerteService.generateStockAlerts();
        
        // Get filtered alerts
        List<AlerteStock> alertes = alerteService.searchAlerts(
            request.getType(),
            request.getPriorite(),
            request.getStatut(),
            request.getIsRead()
        );
        
        // Apply search term filter if provided
        if (request.getSearchTerm() != null && !request.getSearchTerm().trim().isEmpty()) {
            String searchLower = request.getSearchTerm().toLowerCase();
            alertes = alertes.stream()
                .filter(a -> 
                    (a.getTitre() != null && a.getTitre().toLowerCase().contains(searchLower)) ||
                    (a.getMessage() != null && a.getMessage().toLowerCase().contains(searchLower)) ||
                    (a.getArticle() != null && a.getArticle().getNom() != null && 
                     a.getArticle().getNom().toLowerCase().contains(searchLower)))
                .collect(Collectors.toList());
        }
        
        // Convert to DTOs
        List<AlerteStockDto> allAlerteDtos = alertes.stream()
            .map(this::toDto)
            .collect(Collectors.toList());
        
        // Calculate total count
        int totalCount = allAlerteDtos.size();
        
        // Apply pagination
        int page = request.getPage() != null ? request.getPage() : 0;
        int size = request.getSize() != null ? request.getSize() : 15;
        int fromIndex = page * size;
        int toIndex = Math.min(fromIndex + size, totalCount);
        
        List<AlerteStockDto> pagedAlerteDtos = fromIndex < totalCount 
            ? allAlerteDtos.subList(fromIndex, toIndex)
            : List.of();
        
        // Calculate statistics
        Integer unreadCount = Math.toIntExact(allAlerteDtos.stream()
            .filter(a -> !a.getIsRead())
            .count());
        Integer criticalCount = Math.toIntExact(allAlerteDtos.stream()
            .filter(a -> a.getPriorite() == AlertePriorite.CRITICAL)
            .count());
        
        int totalPages = (int) Math.ceil((double) totalCount / size);
        
        GetStockAlertsResponse response = new GetStockAlertsResponse();
        response.setAlertes(pagedAlerteDtos);
        response.setContent(pagedAlerteDtos);  // For compatibility
        response.setTotalCount(totalCount);
        response.setTotalElements((long) totalCount);
        response.setTotalPages(totalPages);
        response.setSize(size);
        response.setNumber(page);
        response.setUnreadCount(unreadCount);
        response.setCriticalCount(criticalCount);
        response.setFirst(page == 0);
        response.setLast(page >= totalPages - 1);
        response.setEmpty(totalCount == 0);
        
        return response;
    }
    
    private AlerteStockDto toDto(AlerteStock alerte) {
        AlerteStockDto dto = new AlerteStockDto();
        dto.setId(alerte.getId());
        dto.setType(alerte.getType());
        dto.setPriorite(alerte.getPriorite());
        dto.setStatut(alerte.getStatut());
        dto.setTitre(alerte.getTitre());
        dto.setMessage(alerte.getMessage());
        dto.setDescription(alerte.getDescription());
        
        if (alerte.getArticle() != null) {
            dto.setArticleId(alerte.getArticle().getId());
            dto.setArticleNom(alerte.getArticle().getNom());
            dto.setArticleSku(alerte.getArticle().getSku());
        }
        
        if (alerte.getEntrepot() != null) {
            dto.setEntrepotId(alerte.getEntrepot().getId());
            dto.setEntrepotNom(alerte.getEntrepot().getNom());
        }
        
        dto.setQuantiteActuelle(alerte.getQuantiteActuelle());
        dto.setSeuilMinimum(alerte.getSeuilMinimum());
        dto.setSeuilCritique(alerte.getSeuilCritique());
        dto.setDateCreation(alerte.getDateCreation());
        dto.setDateModification(alerte.getDateModification());
        dto.setIsRead(alerte.getIsRead());
        dto.setIsArchived(alerte.getIsArchived());
        
        return dto;
    }
}
