package com.example.BacK.application.g_Fournisseur.Command.tranchePaiement.addTranchePaiement;

import com.example.BacK.application.exceptions.TranchesExceedTotalAmountException;
import com.example.BacK.application.interfaces.g_Fournisseur.dettesFournisseur.IDettesFournisseurRepositoryService;
import com.example.BacK.application.interfaces.g_Fournisseur.tranchePaiement.ITranchePaiementRepositoryService;
import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Fournisseur.DettesFournisseur;
import com.example.BacK.domain.g_Fournisseur.TranchePaiement;
import org.springframework.stereotype.Component;

@Component
public class AddTranchePaiementHandler implements RequestHandler<AddTranchePaiementCommand, AddTranchePaiementResponse> {
    
    private final ITranchePaiementRepositoryService tranchePaiementService;
    private final IDettesFournisseurRepositoryService dettesFournisseurService;
    
    public AddTranchePaiementHandler(ITranchePaiementRepositoryService tranchePaiementService,
                                   IDettesFournisseurRepositoryService dettesFournisseurService) {
        this.tranchePaiementService = tranchePaiementService;
        this.dettesFournisseurService = dettesFournisseurService;
    }
    
    @Override
    public AddTranchePaiementResponse handle(AddTranchePaiementCommand command) {
        try {
            
            DettesFournisseur dette = dettesFournisseurService.getById(command.getDettesFournisseurId());
            if (dette == null) {
                return AddTranchePaiementResponse.error("Dette fournisseur non trouvée");
            }
            

            Double totalTranchesActuel = dette.getTranchesPaiement().stream()
                .mapToDouble(TranchePaiement::getMontant)
                .sum();
            

            Double nouveauTotal = totalTranchesActuel + command.getMontant();
            if (nouveauTotal > dette.getMontantTotal()) {
                throw TranchesExceedTotalAmountException.withAmounts(nouveauTotal, dette.getMontantTotal());
            }
            

            TranchePaiement tranche = new TranchePaiement();
            tranche.setMontant(command.getMontant());
            tranche.setDateEcheance(command.getDateEcheance());
            tranche.setEstPaye(command.getEstPaye());
            tranche.setDettesFournisseur(dette);
            

            TranchePaiement savedTranche = tranchePaiementService.save(tranche);
            
            return AddTranchePaiementResponse.success(savedTranche.getId());
            
        } catch (TranchesExceedTotalAmountException e) {
            return AddTranchePaiementResponse.error(e.getMessage());
        } catch (Exception e) {
            return AddTranchePaiementResponse.error("Erreur lors de la création de la tranche de paiement: " + e.getMessage());
        }
    }
}