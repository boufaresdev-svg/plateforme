package com.example.BacK.application.g_Fournisseur.Command.dettesFournisseur.updateDettesFournisseur;

import com.example.BacK.application.exceptions.DettesFournisseurNotFoundException;
import com.example.BacK.application.exceptions.FournisseurNotFoundException;
import com.example.BacK.application.exceptions.DuplicateNumeroFactureException;
import com.example.BacK.application.interfaces.g_Fournisseur.dettesFournisseur.IDettesFournisseurRepositoryService;
import com.example.BacK.application.interfaces.g_Fournisseur.fournisseur.IFournisseurRepositoryService;
import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Fournisseur.DettesFournisseur;
import com.example.BacK.domain.g_Fournisseur.Fournisseur;
import com.example.BacK.domain.g_Fournisseur.TranchePaiement;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component("UpdateDettesFournisseurHandler")
public class UpdateDettesFournisseurHandler implements RequestHandler<UpdateDettesFournisseurCommand, UpdateDettesFournisseurResponse> {

    private final IDettesFournisseurRepositoryService dettesFournisseurRepositoryService;
    private final IFournisseurRepositoryService fournisseurRepositoryService;

    public UpdateDettesFournisseurHandler(IDettesFournisseurRepositoryService dettesFournisseurRepositoryService,
                                          IFournisseurRepositoryService fournisseurRepositoryService) {
        this.dettesFournisseurRepositoryService = dettesFournisseurRepositoryService;
        this.fournisseurRepositoryService = fournisseurRepositoryService;
    }

    @Override
    public UpdateDettesFournisseurResponse handle(UpdateDettesFournisseurCommand command) {
        
        if (command.getId() == null || command.getId().trim().isEmpty()) {
            throw new IllegalArgumentException("L'ID de la dette est obligatoire");
        }
        
        DettesFournisseur existingDette = dettesFournisseurRepositoryService.getById(command.getId());
        
        if (existingDette == null) {
            throw DettesFournisseurNotFoundException.withId(command.getId());
        }
        

        if (command.getNumeroFacture() != null) {
            
            if (!command.getNumeroFacture().equals(existingDette.getNumeroFacture()) && 
                dettesFournisseurRepositoryService.existsByNumeroFacture(command.getNumeroFacture())) {
                throw DuplicateNumeroFactureException.withNumeroFacture(command.getNumeroFacture());
            }
            existingDette.setNumeroFacture(command.getNumeroFacture());
        }
        if (command.getTitre() != null) {
            existingDette.setTitre(command.getTitre());
        }
        if (command.getDescription() != null) {
            existingDette.setDescription(command.getDescription());
        }
        if (command.getMontantTotal() != null) {
            existingDette.setMontantTotal(command.getMontantTotal());
        }
        if (command.getEstPaye() != null) {
            if (Boolean.TRUE.equals(command.getEstPaye()) && !Boolean.TRUE.equals(existingDette.getEstPaye())) {
                // Marquer la dette comme payée
                marquerDetteCommePaye(existingDette);
            } else if (Boolean.FALSE.equals(command.getEstPaye()) && Boolean.TRUE.equals(existingDette.getEstPaye())) {
                // Marquer la dette comme non payée
                marquerDetteCommeNonPaye(existingDette);
            } else {
                existingDette.setEstPaye(command.getEstPaye());
            }
        }
        if (command.getType() != null) {
            existingDette.setType(command.getType());
        }
        if (command.getDatePaiementPrevue() != null) {
            existingDette.setDatePaiementPrevue(command.getDatePaiementPrevue());
        }
        if (command.getDatePaiementReelle() != null) {
            existingDette.setDatePaiementReelle(command.getDatePaiementReelle());
        }
        

        if (command.getFournisseurId() != null && 
            (existingDette.getFournisseur() == null || 
             !command.getFournisseurId().equals(existingDette.getFournisseur().getId()))) {
            Fournisseur fournisseur = fournisseurRepositoryService.getById(command.getFournisseurId());
            if (fournisseur == null) {
                throw FournisseurNotFoundException.withId(command.getFournisseurId());
            }
            existingDette.setFournisseur(fournisseur);
        }
        
        this.dettesFournisseurRepositoryService.update(existingDette);
        return new UpdateDettesFournisseurResponse(existingDette.getId());
    }
    
    private void marquerDetteCommePaye(DettesFournisseur dette) {
        dette.setEstPaye(true);
        dette.setDatePaiementReelle(LocalDate.now());
        
        // Marquer toutes les tranches comme payées
        if (dette.getTranchesPaiement() != null && !dette.getTranchesPaiement().isEmpty()) {
            for (TranchePaiement tranche : dette.getTranchesPaiement()) {
                if (!Boolean.TRUE.equals(tranche.getEstPaye())) {
                    tranche.setEstPaye(true);
                    tranche.setDatePaiement(LocalDate.now());
                }
            }
        }
    }
    
    private void marquerDetteCommeNonPaye(DettesFournisseur dette) {
        dette.setEstPaye(false);
        dette.setDatePaiementReelle(null);
        
        // Annuler le paiement de la dernière tranche
        if (dette.getTranchesPaiement() != null && !dette.getTranchesPaiement().isEmpty()) {
            var tranches = dette.getTranchesPaiement();
            TranchePaiement derniereTranche = tranches.get(tranches.size() - 1);
            if (Boolean.TRUE.equals(derniereTranche.getEstPaye())) {
                derniereTranche.setEstPaye(false);
                derniereTranche.setDatePaiement(null);
            }
        }
    }
}