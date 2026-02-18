package com.example.BacK.application.g_Fournisseur.Command.tranchePaiement.payTranchePaiement;

import com.example.BacK.application.interfaces.g_Fournisseur.tranchePaiement.ITranchePaiementRepositoryService;
import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Fournisseur.TranchePaiement;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

@Component
public class PayTranchePaiementHandler implements RequestHandler<PayTranchePaiementCommand, PayTranchePaiementResponse> {
    
    private final ITranchePaiementRepositoryService tranchePaiementService;
    
    public PayTranchePaiementHandler(ITranchePaiementRepositoryService tranchePaiementService) {
        this.tranchePaiementService = tranchePaiementService;
    }
    
    @Override
    public PayTranchePaiementResponse handle(PayTranchePaiementCommand command) {
        try {
            Optional<TranchePaiement> trancheOpt = tranchePaiementService.findById(command.getTrancheId());
            if (trancheOpt.isEmpty()) {
                return PayTranchePaiementResponse.error("Tranche de paiement introuvable");
            }
            
            TranchePaiement tranche = trancheOpt.get();
            
            // Logique métier pour payer la tranche
            payerTranche(tranche);
            
            tranchePaiementService.save(tranche);
            return PayTranchePaiementResponse.success(tranche.getId());
        } catch (Exception e) {
            return PayTranchePaiementResponse.error("Erreur lors du paiement de la tranche: " + e.getMessage());
        }
    }
    
    private void payerTranche(TranchePaiement tranche) {
        tranche.setEstPaye(true);
        tranche.setDatePaiement(LocalDate.now());
    }
}