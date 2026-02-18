package com.example.BacK.infrastructure.g_Stock.Repositories;

import com.example.BacK.domain.g_Stock.Entrepot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EntrepotRepository extends JpaRepository<Entrepot, String> {
    
    Optional<Entrepot> findByNom(String nom);
    
    List<Entrepot> findByEstActifTrue();
    
    List<Entrepot> findByEstActifFalse();
    
    List<Entrepot> findByVille(String ville);
    
    List<Entrepot> findByStatut(String statut);
    
    @Query("SELECT e FROM Entrepot e WHERE " +
           "(:searchTerm IS NULL OR :searchTerm = '' OR " +
           "LOWER(e.nom) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(e.ville) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(e.adresse) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(e.responsable) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "AND (:ville IS NULL OR :ville = '' OR e.ville = :ville) " +
           "AND (:statut IS NULL OR :statut = '' OR e.statut = :statut) " +
           "AND (:estActif IS NULL OR e.estActif = :estActif)")
    List<Entrepot> search(@Param("searchTerm") String searchTerm,
                          @Param("ville") String ville,
                          @Param("statut") String statut,
                          @Param("estActif") Boolean estActif);
}
