    
    
    
    package com.example.BacK.application.g_Stock.Query.stockLot;

import com.example.BacK.application.interfaces.g_Stock.stockLot.IStockLotRepositoryService;
import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Stock.StockLot;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class GetStockLotHandler implements RequestHandler<GetStockLotQuery, Object> {

    private final IStockLotRepositoryService stockLotRepositoryService;

    public GetStockLotHandler(IStockLotRepositoryService stockLotRepositoryService) {
        this.stockLotRepositoryService = stockLotRepositoryService;
    }

    @Override
    public Object handle(GetStockLotQuery query) {
        // If ID is provided, return single lot
        if (query.getId() != null && !query.getId().isEmpty()) {
            StockLot lot = stockLotRepositoryService.getById(query.getId());
            if (lot == null) {
                return null;
            }
            return mapToResponse(lot);
        }

        // Otherwise return list with filters
        List<StockLot> lots;
        
        if (query.getArticleId() != null && query.getEntrepotId() != null) {
            lots = stockLotRepositoryService.getByArticleIdAndEntrepotId(
                query.getArticleId(), 
                query.getEntrepotId()
            );
        } else if (query.getArticleId() != null) {
            lots = stockLotRepositoryService.getByArticleId(query.getArticleId());
        } else if (query.getEntrepotId() != null) {
            lots = stockLotRepositoryService.getByEntrepotId(query.getEntrepotId());
        } else {
            lots = stockLotRepositoryService.getAll();
        }

        // Apply filters
        if (query.getEstActif() != null) {
            lots = lots.stream()
                .filter(lot -> lot.getEstActif() == query.getEstActif())
                .collect(Collectors.toList());
        }

        if (Boolean.TRUE.equals(query.getAvailableOnly())) {
            lots = lots.stream()
                .filter(lot -> lot.getQuantiteDisponible() > 0)
                .collect(Collectors.toList());
        }

        return lots.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    private GetStockLotResponse mapToResponse(StockLot lot) {
        GetStockLotResponse response = new GetStockLotResponse();
        response.setId(lot.getId());
        response.setNumeroLot(lot.getNumeroLot());
        response.setArticleId(lot.getArticle().getId());
        response.setArticleNom(lot.getArticle().getNom());
        response.setArticleSku(lot.getArticle().getSku());
        response.setEntrepotId(lot.getEntrepot().getId());
        response.setEntrepotNom(lot.getEntrepot().getNom());
        response.setQuantiteInitiale(lot.getQuantiteInitiale());
        response.setQuantiteActuelle(lot.getQuantiteActuelle());
        response.setQuantiteDisponible(lot.getQuantiteDisponible());
        response.setQuantiteReservee(lot.getQuantiteReservee());
        response.setDateAchat(lot.getDateAchat());
        response.setDateExpiration(lot.getDateExpiration());
        response.setPrixAchatUnitaire(lot.getPrixAchatUnitaire());
        response.setPrixVenteUnitaire(lot.getPrixVenteUnitaire());
        response.setValeurTotale(lot.getValeurTotale());
        response.setNumeroFacture(lot.getNumeroFacture());
        response.setFactureUrl(lot.getFactureUrl());
        response.setReferenceFournisseur(lot.getReferenceFournisseur());
        response.setEstActif(lot.getEstActif());
        return response;
    }
}
