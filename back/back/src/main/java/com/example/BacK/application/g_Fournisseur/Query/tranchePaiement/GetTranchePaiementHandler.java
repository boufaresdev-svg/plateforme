package com.example.BacK.application.g_Fournisseur.Query.tranchePaiement;

import com.example.BacK.application.interfaces.g_Fournisseur.tranchePaiement.ITranchePaiementRepositoryService;
import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Fournisseur.TranchePaiement;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class GetTranchePaiementHandler implements RequestHandler<GetTranchePaiementQuery, List<GetTranchePaiementResponse>> {
    
    private final ITranchePaiementRepositoryService tranchePaiementService;
    
    public GetTranchePaiementHandler(ITranchePaiementRepositoryService tranchePaiementService) {
        this.tranchePaiementService = tranchePaiementService;
    }
    
    @Override
    public List<GetTranchePaiementResponse> handle(GetTranchePaiementQuery query) {
        List<TranchePaiement> tranches;
        
        if (query.getId() != null && !query.getId().isEmpty()) {
            // Handle get by ID
            return tranchePaiementService.findById(query.getId())
                .map(tranche -> List.of(mapToResponse(tranche)))
                .orElse(List.of());
        } else if (query.getDettesFournisseurId() != null && !query.getDettesFournisseurId().isEmpty()) {
            tranches = tranchePaiementService.findByDettesFournisseurId(query.getDettesFournisseurId());
        } else if (query.getFournisseurId() != null && !query.getFournisseurId().isEmpty()) {
            tranches = tranchePaiementService.findByFournisseurId(query.getFournisseurId());
        } else if (Boolean.TRUE.equals(query.getEnRetard())) {
            tranches = tranchePaiementService.findTrancheEnRetard();
        } else if (Boolean.FALSE.equals(query.getEstPaye())) {
            tranches = tranchePaiementService.findTrancheNonPayee();
        } else {
            tranches = tranchePaiementService.findAll();
        }
        
        return tranches.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    private GetTranchePaiementResponse mapToResponse(TranchePaiement tranche) {
        GetTranchePaiementResponse response = new GetTranchePaiementResponse();
        response.setId(tranche.getId());
        response.setMontant(tranche.getMontant());
        response.setDateEcheance(tranche.getDateEcheance());
        response.setDatePaiement(tranche.getDatePaiement());
        response.setEstPaye(tranche.getEstPaye());
        response.setEstEnRetard(estTrancheEnRetard(tranche));
        response.setDettesFournisseurId(tranche.getDettesFournisseur().getId());
        response.setNumeroFacture(tranche.getDettesFournisseur().getNumeroFacture());
        response.setFournisseurNom(tranche.getDettesFournisseur().getFournisseur().getNom());
        return response;
    }
    
    private boolean estTrancheEnRetard(TranchePaiement tranche) {
        if (Boolean.TRUE.equals(tranche.getEstPaye())) {
            return false;
        }
        return tranche.getDateEcheance() != null && LocalDate.now().isAfter(tranche.getDateEcheance());
    }
}