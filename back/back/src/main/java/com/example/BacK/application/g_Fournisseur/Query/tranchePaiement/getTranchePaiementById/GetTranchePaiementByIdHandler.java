package com.example.BacK.application.g_Fournisseur.Query.tranchePaiement.getTranchePaiementById;

import com.example.BacK.application.interfaces.g_Fournisseur.tranchePaiement.ITranchePaiementRepositoryService;
import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Fournisseur.TranchePaiement;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

@Component
public class GetTranchePaiementByIdHandler implements RequestHandler<GetTranchePaiementByIdQuery, GetTranchePaiementByIdResponse> {
    
    private final ITranchePaiementRepositoryService tranchePaiementService;
    private final ModelMapper modelMapper;
    
    public GetTranchePaiementByIdHandler(ITranchePaiementRepositoryService tranchePaiementService,
                                        ModelMapper modelMapper) {
        this.tranchePaiementService = tranchePaiementService;
        this.modelMapper = modelMapper;
    }
    
    @Override
    public GetTranchePaiementByIdResponse handle(GetTranchePaiementByIdQuery query) {
        Optional<TranchePaiement> trancheOpt = tranchePaiementService.findById(query.getId());
        
        if (trancheOpt.isEmpty()) {
            return null;
        }
        
        TranchePaiement tranche = trancheOpt.get();
        GetTranchePaiementByIdResponse response = modelMapper.map(tranche, GetTranchePaiementByIdResponse.class);
        

        if (tranche.getDettesFournisseur() != null) {
            response.setDettesFournisseurId(tranche.getDettesFournisseur().getId());
            response.setNumeroFacture(tranche.getDettesFournisseur().getNumeroFacture());
            

            if (tranche.getDettesFournisseur().getFournisseur() != null) {
                response.setFournisseurNom(tranche.getDettesFournisseur().getFournisseur().getNom());
            }
        }
        

        response.setEstEnRetard(estTrancheEnRetard(tranche));
        
        return response;
    }
    
    private boolean estTrancheEnRetard(TranchePaiement tranche) {
        if (Boolean.TRUE.equals(tranche.getEstPaye())) {
            return false;
        }
        return tranche.getDateEcheance() != null && LocalDate.now().isAfter(tranche.getDateEcheance());
    }
}
