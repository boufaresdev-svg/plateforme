package com.example.BacK.application.g_Fournisseur.Query.dettesFournisseur.getSoldeRestant;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.application.interfaces.g_Fournisseur.dettesFournisseur.IDettesFournisseurRepositoryService;
import com.example.BacK.domain.g_Fournisseur.DettesFournisseur;
import com.example.BacK.domain.g_Fournisseur.TranchePaiement;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component("GetSoldeRestantDetteHandler")
public class GetSoldeRestantDetteHandler implements RequestHandler<GetSoldeRestantDetteQuery, GetSoldeRestantDetteResponse> {

    private final IDettesFournisseurRepositoryService dettesFournisseurRepositoryService;

    public GetSoldeRestantDetteHandler(
            IDettesFournisseurRepositoryService dettesFournisseurRepositoryService) {
        this.dettesFournisseurRepositoryService = dettesFournisseurRepositoryService;
    }

    @Override
    public GetSoldeRestantDetteResponse handle(GetSoldeRestantDetteQuery query) {
        DettesFournisseur dette = dettesFournisseurRepositoryService.getById(query.getDetteId());
        if (dette == null) {
            throw new IllegalArgumentException("Dette introuvable avec l'ID: " + query.getDetteId());
        }

        Double soldeRestant = obtenirSoldeRestant(dette);
        Boolean estEnRetard = estEnRetard(dette);

        return new GetSoldeRestantDetteResponse(
            dette.getId(),
            dette.getNumeroFacture(),
            dette.getMontantTotal(),
            soldeRestant,
            dette.getEstPaye(),
            estEnRetard
        );
    }
    
    private Double obtenirSoldeRestant(DettesFournisseur dette) {
        if (Boolean.TRUE.equals(dette.getEstPaye())) {
            return 0.0;
        }

        if (dette.getMontantTotal() == null) {
            return 0.0;
        }

        double montantPaye = dette.getTranchesPaiement().stream()
            .filter(TranchePaiement::getEstPaye)
            .mapToDouble(TranchePaiement::getMontant)
            .sum();
        
        double soldeRestant = dette.getMontantTotal() - montantPaye;
        return Math.max(0.0, soldeRestant);
    }
    
    private Boolean estEnRetard(DettesFournisseur dette) {
        if (Boolean.TRUE.equals(dette.getEstPaye())) {
            return false;
        }
        
        return dette.getTranchesPaiement().stream()
            .anyMatch(this::estTrancheEnRetard);
    }
    
    private boolean estTrancheEnRetard(TranchePaiement tranche) {
        if (Boolean.TRUE.equals(tranche.getEstPaye())) {
            return false;
        }
        return tranche.getDateEcheance() != null && LocalDate.now().isAfter(tranche.getDateEcheance());
    }
}