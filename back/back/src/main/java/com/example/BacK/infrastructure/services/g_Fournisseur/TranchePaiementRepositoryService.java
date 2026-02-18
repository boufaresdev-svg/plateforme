package com.example.BacK.infrastructure.services.g_Fournisseur;

import com.example.BacK.application.interfaces.g_Fournisseur.tranchePaiement.ITranchePaiementRepositoryService;
import com.example.BacK.domain.g_Fournisseur.TranchePaiement;
import com.example.BacK.infrastructure.repository.g_Fournisseur.TranchePaiementRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TranchePaiementRepositoryService implements ITranchePaiementRepositoryService {
    
    private final TranchePaiementRepository tranchePaiementRepository;
    
    public TranchePaiementRepositoryService(TranchePaiementRepository tranchePaiementRepository) {
        this.tranchePaiementRepository = tranchePaiementRepository;
    }
    
    @Override
    public TranchePaiement save(TranchePaiement tranchePaiement) {
        return tranchePaiementRepository.save(tranchePaiement);
    }
    
    @Override
    public Optional<TranchePaiement> findById(String id) {
        return tranchePaiementRepository.findById(id);
    }
    
    @Override
    public List<TranchePaiement> findAll() {
        return tranchePaiementRepository.findAll();
    }
    
    @Override
    public void deleteById(String id) {
        tranchePaiementRepository.deleteById(id);
    }
    
    @Override
    public List<TranchePaiement> findByDettesFournisseurId(String dettesFournisseurId) {
        return tranchePaiementRepository.findByDettesFournisseurId(dettesFournisseurId);
    }
    
    @Override
    public List<TranchePaiement> findTrancheNonPayee() {
        return tranchePaiementRepository.findByEstPayeFalse();
    }
    
    @Override
    public List<TranchePaiement> findTrancheEnRetard() {
        return tranchePaiementRepository.findTrancheEnRetard(LocalDate.now());
    }
    
    @Override
    public List<TranchePaiement> findByFournisseurId(String fournisseurId) {
        return tranchePaiementRepository.findByFournisseurId(fournisseurId);
    }
    
    @Override
    public TranchePaiement payerTranche(String trancheId) {
        Optional<TranchePaiement> trancheOpt = tranchePaiementRepository.findById(trancheId);
        if (trancheOpt.isPresent()) {
            TranchePaiement tranche = trancheOpt.get();
            // Business logic for paying the tranche
            tranche.setEstPaye(true);
            tranche.setDatePaiement(LocalDate.now());
            return tranchePaiementRepository.save(tranche);
        }
        throw new RuntimeException("Tranche de paiement non trouvée avec l'ID: " + trancheId);
    }
}