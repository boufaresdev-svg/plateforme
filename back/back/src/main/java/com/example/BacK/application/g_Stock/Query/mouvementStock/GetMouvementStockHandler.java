package com.example.BacK.application.g_Stock.Query.mouvementStock;

import com.example.BacK.application.interfaces.g_Stock.mouvementStock.IMouvementStockRepositoryService;
import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Stock.MouvementStock;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component("GetMouvementStockHandler")
public class GetMouvementStockHandler implements RequestHandler<GetMouvementStockQuery, List<GetMouvementStockResponse>> {

    private final IMouvementStockRepositoryService mouvementStockRepositoryService;
    private final ModelMapper modelMapper;

    public GetMouvementStockHandler(IMouvementStockRepositoryService mouvementStockRepositoryService, ModelMapper modelMapper) {
        this.mouvementStockRepositoryService = mouvementStockRepositoryService;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<GetMouvementStockResponse> handle(GetMouvementStockQuery query) {
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
        // Filter by utilisateur
        else if (query.getUtilisateurId() != null && !query.getUtilisateurId().isEmpty()) {
            mouvements = mouvementStockRepositoryService.getByUtilisateurId(query.getUtilisateurId());
        }
        // Filter by type
        else if (query.getTypeMouvement() != null) {
            mouvements = mouvementStockRepositoryService.getByTypeMouvement(query.getTypeMouvement());
        }
        // Filter by statut
        else if (query.getStatut() != null) {
            mouvements = mouvementStockRepositoryService.getByStatut(query.getStatut());
        }
        // Filter by date range
        else if (query.getStartDate() != null && query.getEndDate() != null) {
            mouvements = mouvementStockRepositoryService.getByDateMouvementBetween(
                query.getStartDate(), 
                query.getEndDate()
            );
        }
        // Filter by reference
        else if (query.getReference() != null && !query.getReference().isEmpty()) {
            mouvements = mouvementStockRepositoryService.getByReference(query.getReference());
        }
        // Get all
        else {
            mouvements = mouvementStockRepositoryService.getAll();
        }

        // Apply additional stream filters for category, marque, entrepot
        return mouvements.stream()
                .filter(m -> query.getCategorieId() == null || 
                    (m.getArticle() != null && m.getArticle().getCategorie() != null && 
                     m.getArticle().getCategorie().getId().equals(query.getCategorieId())))
                .filter(m -> query.getMarqueId() == null || 
                    (m.getArticle() != null && m.getArticle().getMarque() != null && 
                     m.getArticle().getMarque().getId().equals(query.getMarqueId())))
                .filter(m -> query.getEntrepotId() == null || 
                    ((m.getSourceEntrepot() != null && m.getSourceEntrepot().getId().equals(query.getEntrepotId())) ||
                     (m.getDestinationEntrepot() != null && m.getDestinationEntrepot().getId().equals(query.getEntrepotId()))))
                .sorted((m1, m2) -> {
                    if (m2.getDateMouvement() == null) return -1;
                    if (m1.getDateMouvement() == null) return 1;
                    return m2.getDateMouvement().compareTo(m1.getDateMouvement());
                })
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private GetMouvementStockResponse mapToResponse(MouvementStock mouvement) {
        GetMouvementStockResponse response = modelMapper.map(mouvement, GetMouvementStockResponse.class);
        
        // Map article details
        if (mouvement.getArticle() != null) {
            response.setArticleId(mouvement.getArticle().getId());
            response.setArticleNom(mouvement.getArticle().getNom());
            response.setArticleSku(mouvement.getArticle().getSku());
        }
        
        // Map source warehouse details
        if (mouvement.getSourceEntrepot() != null) {
            response.setSourceEntrepotId(mouvement.getSourceEntrepot().getId());
            response.setSourceEntrepotNom(mouvement.getSourceEntrepot().getNom());
        }
        
        // Map destination warehouse details
        if (mouvement.getDestinationEntrepot() != null) {
            response.setDestinationEntrepotId(mouvement.getDestinationEntrepot().getId());
            response.setDestinationEntrepotNom(mouvement.getDestinationEntrepot().getNom());
        }
        
        // Map user details
        if (mouvement.getUtilisateur() != null) {
            response.setUtilisateurId(mouvement.getUtilisateur().getId());
            // Add username if available
            // response.setUtilisateurNom(mouvement.getUtilisateur().getUsername());
        }
        
        // Map stock lot ID
        response.setStockLotId(mouvement.getStockLotId());
        
        return response;
    }
}
