package com.example.BacK.application.g_Fournisseur.Command.dettesFournisseur.enregistrerPaiement;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.application.interfaces.g_Fournisseur.dettesFournisseur.IDettesFournisseurRepositoryService;
import com.example.BacK.domain.g_Fournisseur.DettesFournisseur;
import com.example.BacK.domain.g_Fournisseur.TranchePaiement;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component("EnregistrerPaiementDetteHandler")
public class EnregistrerPaiementDetteHandler implements RequestHandler<EnregistrerPaiementDetteCommand, EnregistrerPaiementDetteResponse> {

    private final IDettesFournisseurRepositoryService dettesFournisseurRepositoryService;

    public EnregistrerPaiementDetteHandler(
            IDettesFournisseurRepositoryService dettesFournisseurRepositoryService) {
        this.dettesFournisseurRepositoryService = dettesFournisseurRepositoryService;
    }

    @Override
    public EnregistrerPaiementDetteResponse handle(EnregistrerPaiementDetteCommand command) {
        // Récupérer la dette
        DettesFournisseur dette = dettesFournisseurRepositoryService.getById(command.getDetteId());
        if (dette == null) {
            throw new IllegalArgumentException("Dette introuvable avec l'ID: " + command.getDetteId());
        }

        // Utiliser la logique métier pour enregistrer le paiement
        enregistrerPaiement(dette, command.getMontant());

        // Sauvegarder les modifications
        dettesFournisseurRepositoryService.update(dette);

        // Calculer le nouveau solde restant
        Double soldeRestant = obtenirSoldeRestant(dette);

        return new EnregistrerPaiementDetteResponse(
            dette.getId(),
            dette.getEstPaye(),
            soldeRestant,
            "Paiement enregistré avec succès"
        );
    }
    
    private void enregistrerPaiement(DettesFournisseur dette, Double montant) {
        if (montant == null || montant <= 0) {
            throw new IllegalArgumentException("Le montant doit être positif");
        }
        
        if (dette.getEstPaye()) {
            throw new IllegalStateException("Cette dette est déjà payée");
        }
        
        double montantRestant = montant;
        
        for (TranchePaiement tranche : dette.getTranchesPaiement()) {
            if (!tranche.getEstPaye() && montantRestant > 0) {
                if (montantRestant >= tranche.getMontant()) {
                    tranche.setEstPaye(true);
                    tranche.setDatePaiement(LocalDate.now());
                    montantRestant -= tranche.getMontant();
                }
            }
        }
        
        // Vérifier si toutes les tranches sont payées
        boolean toutesTranchesPaye = dette.getTranchesPaiement().stream()
            .allMatch(TranchePaiement::getEstPaye);
        
        if (toutesTranchesPaye) {
            dette.setEstPaye(true);
            dette.setDatePaiementReelle(LocalDate.now());
        }
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
}