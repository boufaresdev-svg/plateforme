package com.example.BacK.application.g_Stock.Query.tracabilite;

import com.example.BacK.application.interfaces.g_Stock.ajustementStock.IAjustementStockRepositoryService;
import com.example.BacK.application.interfaces.g_Stock.mouvementStock.IMouvementStockRepositoryService;
import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Stock.AjustementStock;
import com.example.BacK.domain.g_Stock.MouvementStock;
import com.example.BacK.domain.g_Stock.enumEntity.TypeMouvement;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component("GetTracabiliteHandler")
public class GetTracabiliteHandler implements RequestHandler<GetTracabiliteQuery, List<GetTracabiliteResponse>> {

    private final IMouvementStockRepositoryService mouvementStockRepositoryService;
    private final IAjustementStockRepositoryService ajustementStockRepositoryService;

    public GetTracabiliteHandler(IMouvementStockRepositoryService mouvementStockRepositoryService,
                                 IAjustementStockRepositoryService ajustementStockRepositoryService) {
        this.mouvementStockRepositoryService = mouvementStockRepositoryService;
        this.ajustementStockRepositoryService = ajustementStockRepositoryService;
    }

    @Override
    public List<GetTracabiliteResponse> handle(GetTracabiliteQuery query) {
        List<GetTracabiliteResponse> results = new ArrayList<>();
        
        // Get regular mouvements
        List<MouvementStock> mouvements = getMouvements(query);
        results.addAll(mouvements.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList()));
        
        // Get ajustements if no specific type filter or if AJUSTEMENT type is requested
        if (query.getTypeMouvement() == null || query.getTypeMouvement() == TypeMouvement.AJUSTEMENT) {
            List<AjustementStock> ajustements = getAjustements(query);
            results.addAll(ajustements.stream()
                    .map(this::mapAjustementToResponse)
                    .collect(Collectors.toList()));
        }
        
        // Sort by date descending (most recent first)
        return results.stream()
                .sorted((r1, r2) -> {
                    if (r2.getDateMouvement() == null) return -1;
                    if (r1.getDateMouvement() == null) return 1;
                    return r2.getDateMouvement().compareTo(r1.getDateMouvement());
                })
                .collect(Collectors.toList());
    }
    
    private List<MouvementStock> getMouvements(GetTracabiliteQuery query) {
        List<MouvementStock> mouvements;

        // If ID is provided, get specific movement
        if (query.getId() != null && !query.getId().isEmpty()) {
            MouvementStock mouvement = mouvementStockRepositoryService.getById(query.getId());
            if (mouvement != null) {
                mouvements = List.of(mouvement);
            } else {
                mouvements = List.of();
            }
        }
        // Filter by article
        else if (query.getArticleId() != null && !query.getArticleId().isEmpty()) {
            mouvements = mouvementStockRepositoryService.getByArticleId(query.getArticleId());
        }
        // Filter by entrepot
        else if (query.getEntrepotId() != null && !query.getEntrepotId().isEmpty()) {
            mouvements = mouvementStockRepositoryService.getBySourceOrDestinationEntrepot(query.getEntrepotId());
        }
        // Filter by utilisateur
        else if (query.getUtilisateurId() != null && !query.getUtilisateurId().isEmpty()) {
            mouvements = mouvementStockRepositoryService.getByUtilisateurId(query.getUtilisateurId());
        }
        // Filter by type
        else if (query.getTypeMouvement() != null) {
            mouvements = mouvementStockRepositoryService.getByTypeMouvement(query.getTypeMouvement());
        }
        // Filter by date range
        else if (query.getStartDate() != null && query.getEndDate() != null) {
            mouvements = mouvementStockRepositoryService.getByDateMouvementBetween(
                query.getStartDate(), 
                query.getEndDate()
            );
        }
        // Get all
        else {
            mouvements = mouvementStockRepositoryService.getAll();
        }

        return mouvements;
    }
    
    private List<AjustementStock> getAjustements(GetTracabiliteQuery query) {
        // Get all ajustements first
        List<AjustementStock> allAjustements = ajustementStockRepositoryService.getAll()
                .stream()
                .map(response -> {
                    AjustementStock ajustement = ajustementStockRepositoryService.getById(response.getId());
                    return ajustement;
                })
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

    private GetTracabiliteResponse mapToResponse(MouvementStock mouvement) {
        GetTracabiliteResponse response = new GetTracabiliteResponse();
        
        response.setId(mouvement.getId());
        response.setTypeMouvement(mouvement.getTypeMouvement());
        response.setQuantite(mouvement.getQuantite() != null ? mouvement.getQuantite().intValue() : 0);
        response.setDateMouvement(mouvement.getDateMouvement());
        response.setReference(mouvement.getReference());
        response.setCreatedAt(mouvement.getCreatedAt());
        response.setCreatedBy(mouvement.getCreatedBy());
        
        // Map article details
        if (mouvement.getArticle() != null) {
            response.setArticleId(mouvement.getArticle().getId());
            response.setArticleNom(mouvement.getArticle().getNom());
            response.setArticleSku(mouvement.getArticle().getSku());
        }
        
        // Map source warehouse details (for entree/sortie/ajustement)
        if (mouvement.getSourceEntrepot() != null) {
            response.setEntrepotId(mouvement.getSourceEntrepot().getId());
            response.setEntrepotNom(mouvement.getSourceEntrepot().getNom());
        }
        
        // For transfer, use destination warehouse
        if (mouvement.getTypeMouvement() != null && 
            mouvement.getTypeMouvement().name().contains("TRANSFERT") &&
            mouvement.getDestinationEntrepot() != null) {
            response.setEntrepotId(mouvement.getDestinationEntrepot().getId());
            response.setEntrepotNom(mouvement.getDestinationEntrepot().getNom());
        }
        
        // Map user details
        if (mouvement.getUtilisateur() != null) {
            response.setUtilisateurId(mouvement.getUtilisateur().getId());
            response.setUtilisateurNom(mouvement.getUtilisateur().getUsername());
        }
        
        // Set unit price - try from mouvement first, then from article
        if (mouvement.getPrixUnitaire() != null) {
            response.setPrixUnitaire(mouvement.getPrixUnitaire());
        } else if (mouvement.getArticle() != null && mouvement.getArticle().getPrixAchat() != null) {
            response.setPrixUnitaire(mouvement.getArticle().getPrixAchat());
        } else if (mouvement.getArticle() != null && mouvement.getArticle().getPrixVente() != null) {
            response.setPrixUnitaire(mouvement.getArticle().getPrixVente());
        }
        
        // Calculate values
        if (response.getPrixUnitaire() != null) {
            response.setValeurTotale(response.getPrixUnitaire() * mouvement.getQuantite());
        }
        
        // Set motif based on movement type
        response.setMotif(generateMotif(mouvement));
        response.setCommentaire(mouvement.getCommentaire());
        
        return response;
    }
    
    private String generateMotif(MouvementStock mouvement) {
        // Use the motif field if it exists
        if (mouvement.getMotif() != null && !mouvement.getMotif().trim().isEmpty()) {
            return mouvement.getMotif();
        }
        
        // Otherwise generate from movement details
        StringBuilder motif = new StringBuilder();
        
        switch (mouvement.getTypeMouvement()) {
            case ENTREE:
                if (mouvement.getTypeEntree() != null) {
                    motif.append(mouvement.getTypeEntree().name().replace("_", " "));
                }
                if (mouvement.getNumeroBonReception() != null) {
                    motif.append(" - Bon: ").append(mouvement.getNumeroBonReception());
                }
                break;
            case SORTIE:
                if (mouvement.getTypeSortie() != null) {
                    motif.append(mouvement.getTypeSortie().name().replace("_", " "));
                }
                if (mouvement.getNumeroBonLivraison() != null) {
                    motif.append(" - Bon: ").append(mouvement.getNumeroBonLivraison());
                }
                break;
            case TRANSFERT:
                if (mouvement.getSourceEntrepot() != null && mouvement.getDestinationEntrepot() != null) {
                    motif.append("Transfert: ")
                         .append(mouvement.getSourceEntrepot().getNom())
                         .append(" → ")
                         .append(mouvement.getDestinationEntrepot().getNom());
                }
                break;
            case AJUSTEMENT:
                motif.append("Ajustement d'inventaire");
                break;
            default:
                motif.append(mouvement.getTypeMouvement().name());
        }
        
        return motif.length() > 0 ? motif.toString() : "N/A";
    }
    
    private GetTracabiliteResponse mapAjustementToResponse(AjustementStock ajustement) {
        GetTracabiliteResponse response = new GetTracabiliteResponse();
        
        response.setId(ajustement.getId());
        response.setTypeMouvement(TypeMouvement.AJUSTEMENT);
        response.setQuantite(ajustement.getAjustement());
        response.setQuantitePrecedente(ajustement.getQuantiteAvant());
        response.setQuantiteActuelle(ajustement.getQuantiteApres());
        response.setDateMouvement(ajustement.getDateAjustement().atTime(LocalTime.now()));
        response.setCreatedAt(ajustement.getCreatedAt());
        
        // Map article details
        if (ajustement.getArticle() != null) {
            response.setArticleId(ajustement.getArticle().getId());
            response.setArticleNom(ajustement.getArticle().getNom());
            response.setArticleSku(ajustement.getArticle().getSku());
        }
        
        // Map warehouse details
        if (ajustement.getEntrepot() != null) {
            response.setEntrepotId(ajustement.getEntrepot().getId());
            response.setEntrepotNom(ajustement.getEntrepot().getNom());
        }
        
        // Map user details
        if (ajustement.getUtilisateur() != null) {
            response.setUtilisateurId(ajustement.getUtilisateur().getId());
            response.setUtilisateurNom(ajustement.getUtilisateur().getUsername());
        }
        
        // Set unit price from article
        if (ajustement.getArticle() != null) {
            if (ajustement.getArticle().getPrixAchat() != null) {
                response.setPrixUnitaire(ajustement.getArticle().getPrixAchat());
            } else if (ajustement.getArticle().getPrixVente() != null) {
                response.setPrixUnitaire(ajustement.getArticle().getPrixVente());
            }
        }
        
        // Calculate values
        if (response.getPrixUnitaire() != null) {
            response.setValeurTotale(response.getPrixUnitaire() * ajustement.getAjustement());
        }
        
        // Set motif from raison if available, otherwise use default
        if (ajustement.getRaison() != null && !ajustement.getRaison().trim().isEmpty()) {
            response.setMotif(ajustement.getRaison());
        } else {
            response.setMotif("Ajustement d'inventaire");
        }
        response.setCommentaire(ajustement.getRaison());
        
        return response;
    }
}
