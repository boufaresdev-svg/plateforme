package com.example.BacK.application.g_Fournisseur.Command.tranchePaiement.deleteTranchePaiement;

import com.example.BacK.application.interfaces.g_Fournisseur.tranchePaiement.ITranchePaiementRepositoryService;
import com.example.BacK.application.mediator.RequestHandler;
import org.springframework.stereotype.Component;

@Component
public class DeleteTranchePaiementHandler implements RequestHandler<DeleteTranchePaiementCommand, DeleteTranchePaiementResponse> {
    
    private final ITranchePaiementRepositoryService tranchePaiementService;
    
    public DeleteTranchePaiementHandler(ITranchePaiementRepositoryService tranchePaiementService) {
        this.tranchePaiementService = tranchePaiementService;
    }
    
    @Override
    public DeleteTranchePaiementResponse handle(DeleteTranchePaiementCommand command) {
        try {
            
            if (tranchePaiementService.findById(command.getId()).isEmpty()) {
                return DeleteTranchePaiementResponse.error("Tranche de paiement non trouvée");
            }
            

            tranchePaiementService.deleteById(command.getId());
            return DeleteTranchePaiementResponse.success();
            
        } catch (Exception e) {
            return DeleteTranchePaiementResponse.error("Erreur lors de la suppression: " + e.getMessage());
        }
    }
}
