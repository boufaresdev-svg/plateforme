package com.example.BacK.application.interfaces.g_Fournisseur.tranchePaiement;

import com.example.BacK.domain.g_Fournisseur.TranchePaiement;

import java.util.List;
import java.util.Optional;

public interface ITranchePaiementRepositoryService {
    
    // Command operations (domain entities)
    TranchePaiement save(TranchePaiement tranchePaiement);
    void deleteById(String id);
    TranchePaiement payerTranche(String trancheId);
    
    // Query operations (return DTOs/responses or domain for internal use)
    Optional<TranchePaiement> findById(String id);
    List<TranchePaiement> findAll();
    List<TranchePaiement> findByDettesFournisseurId(String dettesFournisseurId);
    List<TranchePaiement> findTrancheNonPayee();
    List<TranchePaiement> findTrancheEnRetard();
    List<TranchePaiement> findByFournisseurId(String fournisseurId);
}