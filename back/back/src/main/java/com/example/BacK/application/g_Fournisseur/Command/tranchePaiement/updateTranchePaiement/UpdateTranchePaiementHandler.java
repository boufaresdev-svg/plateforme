package com.example.BacK.application.g_Fournisseur.Command.tranchePaiement.updateTranchePaiement;

import com.example.BacK.application.exceptions.TranchesExceedTotalAmountException;
import com.example.BacK.application.interfaces.g_Fournisseur.tranchePaiement.ITranchePaiementRepositoryService;
import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Fournisseur.DettesFournisseur;
import com.example.BacK.domain.g_Fournisseur.TranchePaiement;
import org.springframework.stereotype.Component;

@Component
public class UpdateTranchePaiementHandler implements RequestHandler<UpdateTranchePaiementCommand, UpdateTranchePaiementResponse> {
    
    private final ITranchePaiementRepositoryService tranchePaiementService;
    
    public UpdateTranchePaiementHandler(ITranchePaiementRepositoryService tranchePaiementService) {
        this.tranchePaiementService = tranchePaiementService;
    }
    
    @Override
    public UpdateTranchePaiementResponse handle(UpdateTranchePaiementCommand command) {
        try {
            TranchePaiement tranche = tranchePaiementService.findById(command.getId())
                .orElseThrow(() -> new RuntimeException("Tranche de paiement non trouvée"));
            
            DettesFournisseur dette = tranche.getDettesFournisseur();
            if (dette == null) {
                return UpdateTranchePaiementResponse.error("Dette fournisseur associée non trouvée");
            }
            
            Double totalTranchesActuel = dette.getTranchesPaiement().stream()
                .filter(t -> !t.getId().equals(command.getId()))
                .mapToDouble(TranchePaiement::getMontant)
                .sum();
            
            Double nouveauTotal = totalTranchesActuel + command.getMontant();
            if (nouveauTotal > dette.getMontantTotal()) {
                throw TranchesExceedTotalAmountException.withAmounts(nouveauTotal, dette.getMontantTotal());
            }
            
            tranche.setMontant(command.getMontant());
            tranche.setDateEcheance(command.getDateEcheance());
            if (command.getEstPaye() != null) {
                tranche.setEstPaye(command.getEstPaye());
            }
            
            TranchePaiement updatedTranche = tranchePaiementService.save(tranche);
            
            return UpdateTranchePaiementResponse.success(updatedTranche.getId());
            
        } catch (TranchesExceedTotalAmountException e) {
            return UpdateTranchePaiementResponse.error(e.getMessage());
        } catch (Exception e) {
            return UpdateTranchePaiementResponse.error("Erreur lors de la mise à jour de la tranche de paiement: " + e.getMessage());
        }
    }
}
