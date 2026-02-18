package com.example.BacK.application.g_Stock.Query.ajustementStock;

import com.example.BacK.application.interfaces.g_Stock.ajustementStock.IAjustementStockRepositoryService;
import com.example.BacK.application.mediator.RequestHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component("GetAjustementStockHandler")
@RequiredArgsConstructor
public class GetAjustementStockHandler implements RequestHandler<GetAjustementStockQuery, List<GetAjustementStockResponse>> {
    
    private final IAjustementStockRepositoryService ajustementStockRepositoryService;
    
    @Override
    public List<GetAjustementStockResponse> handle(GetAjustementStockQuery query) {
        List<GetAjustementStockResponse> ajustements;
        
        // If specific ID is provided, fetch by ID
        if (query.getId() != null && !query.getId().isEmpty()) {
            ajustements = ajustementStockRepositoryService.getAll().stream()
                .filter(a -> a.getId().equals(query.getId()))
                .collect(Collectors.toList());
        }
        // If article ID is provided, filter by article
        else if (query.getArticleId() != null && !query.getArticleId().isEmpty()) {
            ajustements = ajustementStockRepositoryService.getByArticleId(query.getArticleId());
        }
        // If utilisateur ID is provided, filter by utilisateur
        else if (query.getUtilisateurId() != null && !query.getUtilisateurId().isEmpty()) {
            ajustements = ajustementStockRepositoryService.getByUtilisateurId(query.getUtilisateurId());
        }
        // If date range is provided, filter by date range
        else if (query.getStartDate() != null && query.getEndDate() != null) {
            ajustements = ajustementStockRepositoryService.getByDateRange(query.getStartDate(), query.getEndDate());
        }
        // Return all if no filters are applied
        else {
            ajustements = ajustementStockRepositoryService.getAll();
        }
        
        // Apply additional stream filters for category, marque, fournisseur, entrepot
        return ajustements.stream()
                .filter(a -> query.getCategorieId() == null || 
                    (a.getArticleCategorieId() != null && 
                     a.getArticleCategorieId().equals(query.getCategorieId())))
                .filter(a -> query.getMarqueId() == null || 
                    (a.getArticleMarqueId() != null && 
                     a.getArticleMarqueId().equals(query.getMarqueId())))
                .filter(a -> query.getEntrepotId() == null || 
                    (a.getEntrepotId() != null && 
                     a.getEntrepotId().equals(query.getEntrepotId())))
                .collect(Collectors.toList());
    }
}
