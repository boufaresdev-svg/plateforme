package com.example.BacK.application.g_Stock.Query.tracabilite;

import com.example.BacK.application.interfaces.g_Stock.ajustementStock.IAjustementStockRepositoryService;
import com.example.BacK.application.interfaces.g_Stock.mouvementStock.IMouvementStockRepositoryService;
import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Stock.AjustementStock;
import com.example.BacK.domain.g_Stock.MouvementStock;
import com.example.BacK.domain.g_Stock.enumEntity.TypeMouvement;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component("GetTracabiliteStatsHandler")
public class GetTracabiliteStatsHandler implements RequestHandler<GetTracabiliteStatsQuery, GetTracabiliteStatsResponse> {

    private final IMouvementStockRepositoryService mouvementStockRepositoryService;
    private final IAjustementStockRepositoryService ajustementStockRepositoryService;

    public GetTracabiliteStatsHandler(IMouvementStockRepositoryService mouvementStockRepositoryService,
                                      IAjustementStockRepositoryService ajustementStockRepositoryService) {
        this.mouvementStockRepositoryService = mouvementStockRepositoryService;
        this.ajustementStockRepositoryService = ajustementStockRepositoryService;
    }

    @Override
    public GetTracabiliteStatsResponse handle(GetTracabiliteStatsQuery query) {
        List<MouvementStock> mouvements = getMouvements(query);
        List<AjustementStock> ajustements = getAjustements(query);

        GetTracabiliteStatsResponse stats = new GetTracabiliteStatsResponse();
        stats.setTotalActions(mouvements.size() + ajustements.size());
        
        // Calculate total quantity modified from mouvements
        int quantiteTotale = mouvements.stream()
            .filter(m -> m.getQuantite() != null)
            .mapToInt(m -> m.getQuantite().intValue())
            .sum();
        
        // Add quantity from ajustements
        quantiteTotale += ajustements.stream()
            .mapToInt(AjustementStock::getAjustement)
            .sum();
        
        stats.setQuantiteTotaleModifiee(quantiteTotale);
        
        // Count by type
        Map<String, Long> byType = mouvements.stream()
            .collect(Collectors.groupingBy(
                m -> m.getTypeMouvement().name(),
                Collectors.counting()
            ));
        
        // Add ajustements count
        if (!ajustements.isEmpty()) {
            byType.put(TypeMouvement.AJUSTEMENT.name(), (long) ajustements.size());
        }
        
        stats.setActionsParType(byType);
        
        // Count by user from mouvements
        Map<String, Long> byUser = mouvements.stream()
            .filter(m -> m.getUtilisateur() != null && m.getUtilisateur().getUsername() != null)
            .collect(Collectors.groupingBy(
                m -> m.getUtilisateur().getUsername(),
                Collectors.counting()
            ));
        
        // Add users from ajustements
        ajustements.stream()
            .filter(a -> a.getUtilisateur() != null && a.getUtilisateur().getUsername() != null)
            .forEach(a -> {
                String username = a.getUtilisateur().getUsername();
                byUser.put(username, byUser.getOrDefault(username, 0L) + 1);
            });
        
        stats.setActionsParUtilisateur(byUser);
        
        // Calculate total value from mouvements
        double valeurTotale = mouvements.stream()
            .filter(m -> m.getQuantite() != null)
            .mapToDouble(m -> {
                Double prix = m.getPrixUnitaire();
                if (prix == null && m.getArticle() != null) {
                    // Fallback to article price if mouvement price is not set
                    if (m.getArticle().getPrixAchat() != null) {
                        prix = m.getArticle().getPrixAchat();
                    } else if (m.getArticle().getPrixVente() != null) {
                        prix = m.getArticle().getPrixVente();
                    }
                }
                return prix != null ? prix * m.getQuantite() : 0.0;
            })
            .sum();
        
        // Add value from ajustements
        valeurTotale += ajustements.stream()
            .mapToDouble(a -> {
                Double prix = null;
                if (a.getArticle() != null) {
                    if (a.getArticle().getPrixAchat() != null) {
                        prix = a.getArticle().getPrixAchat();
                    } else if (a.getArticle().getPrixVente() != null) {
                        prix = a.getArticle().getPrixVente();
                    }
                }
                return prix != null ? Math.abs(prix * a.getAjustement()) : 0.0;
            })
            .sum();
        
        stats.setValeurTotaleModifiee(valeurTotale);
        
        return stats;
    }
    
    private List<MouvementStock> getMouvements(GetTracabiliteStatsQuery query) {
        List<MouvementStock> mouvements;

        // Apply filters
        if (query.getArticleId() != null && !query.getArticleId().isEmpty()) {
            mouvements = mouvementStockRepositoryService.getByArticleId(query.getArticleId());
        } else if (query.getEntrepotId() != null && !query.getEntrepotId().isEmpty()) {
            mouvements = mouvementStockRepositoryService.getBySourceOrDestinationEntrepot(query.getEntrepotId());
        } else if (query.getUtilisateurId() != null && !query.getUtilisateurId().isEmpty()) {
            mouvements = mouvementStockRepositoryService.getByUtilisateurId(query.getUtilisateurId());
        } else if (query.getTypeMouvement() != null) {
            mouvements = mouvementStockRepositoryService.getByTypeMouvement(query.getTypeMouvement());
        } else if (query.getStartDate() != null && query.getEndDate() != null) {
            mouvements = mouvementStockRepositoryService.getByDateMouvementBetween(
                query.getStartDate(), 
                query.getEndDate()
            );
        } else {
            mouvements = mouvementStockRepositoryService.getAll();
        }

        return mouvements;
    }
    
    private List<AjustementStock> getAjustements(GetTracabiliteStatsQuery query) {
        // Skip ajustements if specific non-AJUSTEMENT type is requested
        if (query.getTypeMouvement() != null && query.getTypeMouvement() != TypeMouvement.AJUSTEMENT) {
            return List.of();
        }
        
        // Get all ajustements first
        List<AjustementStock> allAjustements = ajustementStockRepositoryService.getAll()
                .stream()
                .map(response -> ajustementStockRepositoryService.getById(response.getId()))
                .filter(a -> a != null)
                .collect(Collectors.toList());
        
        // Apply filters
        return allAjustements.stream()
                .filter(a -> {
                    if (query.getArticleId() != null && !query.getArticleId().isEmpty()) {
                        return a.getArticle() != null && a.getArticle().getId().equals(query.getArticleId());
                    }
                    if (query.getEntrepotId() != null && !query.getEntrepotId().isEmpty()) {
                        return a.getEntrepot() != null && a.getEntrepot().getId().equals(query.getEntrepotId());
                    }
                    if (query.getUtilisateurId() != null && !query.getUtilisateurId().isEmpty()) {
                        return a.getUtilisateur() != null && a.getUtilisateur().getId().equals(query.getUtilisateurId());
                    }
                    if (query.getStartDate() != null && query.getEndDate() != null) {
                        LocalDateTime ajustementDateTime = a.getDateAjustement().atStartOfDay();
                        return !ajustementDateTime.isBefore(query.getStartDate()) && 
                               !ajustementDateTime.isAfter(query.getEndDate());
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }
}
