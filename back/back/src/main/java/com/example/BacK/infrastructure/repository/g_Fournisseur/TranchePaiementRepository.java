package com.example.BacK.infrastructure.repository.g_Fournisseur;

import com.example.BacK.domain.g_Fournisseur.TranchePaiement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TranchePaiementRepository extends JpaRepository<TranchePaiement, String> {
    
    List<TranchePaiement> findByDettesFournisseurId(String dettesFournisseurId);
    
    List<TranchePaiement> findByEstPayeFalse();
    
    @Query("SELECT t FROM TranchePaiement t WHERE t.estPaye = false AND t.dateEcheance < :currentDate")
    List<TranchePaiement> findTrancheEnRetard(@Param("currentDate") LocalDate currentDate);
    
    @Query("SELECT t FROM TranchePaiement t WHERE t.dettesFournisseur.fournisseur.id = :fournisseurId")
    List<TranchePaiement> findByFournisseurId(@Param("fournisseurId") String fournisseurId);
}