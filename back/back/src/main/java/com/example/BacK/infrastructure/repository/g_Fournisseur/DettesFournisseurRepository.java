package com.example.BacK.infrastructure.repository.g_Fournisseur;

import com.example.BacK.domain.g_Fournisseur.DettesFournisseur;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DettesFournisseurRepository extends JpaRepository<DettesFournisseur, String> {
    
    @Query("SELECT d FROM DettesFournisseur d JOIN FETCH d.fournisseur WHERE d.fournisseur.id = :fournisseurId")
    List<DettesFournisseur> findByFournisseurId(@Param("fournisseurId") String fournisseurId);
    
    @Query("SELECT d FROM DettesFournisseur d WHERE d.fournisseur.id = :fournisseurId")
    Page<DettesFournisseur> findByFournisseurIdPaginated(@Param("fournisseurId") String fournisseurId, Pageable pageable);
    
    @Query("SELECT d FROM DettesFournisseur d JOIN FETCH d.fournisseur")
    List<DettesFournisseur> findAllWithFournisseur();
    
    @Query("SELECT d FROM DettesFournisseur d")
    Page<DettesFournisseur> findAllPaginated(Pageable pageable);
    
    @Query("SELECT d FROM DettesFournisseur d JOIN FETCH d.fournisseur WHERE d.id = :id")
    Optional<DettesFournisseur> findByIdWithFournisseur(@Param("id") String id);
    
    // Flexible search - if only one parameter is provided, search across multiple fields (OR logic)
    // If multiple parameters are provided, use AND logic between them
    @Query("SELECT DISTINCT d FROM DettesFournisseur d LEFT JOIN FETCH d.fournisseur f WHERE " +
           "(" +
           "  (:searchTerm IS NULL OR :searchTerm = '' OR " +
           "   LOWER(d.numeroFacture) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "   LOWER(d.titre) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "   LOWER(d.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "   LOWER(f.nom) LIKE LOWER(CONCAT('%', :searchTerm, '%'))" +
           "  )" +
           ") AND " +
           "(:estPaye IS NULL OR d.estPaye = :estPaye) AND " +
           "(:fournisseurId IS NULL OR f.id = :fournisseurId) AND " +
           "(:dateDebut IS NULL OR d.datePaiementPrevue >= :dateDebut) AND " +
           "(:dateFin IS NULL OR d.datePaiementPrevue <= :dateFin)")
    List<DettesFournisseur> findByFlexibleSearch(
        @Param("searchTerm") String searchTerm,
        @Param("estPaye") Boolean estPaye,
        @Param("fournisseurId") String fournisseurId,
        @Param("dateDebut") LocalDate dateDebut,
        @Param("dateFin") LocalDate dateFin
    );
    
    // Paginated version of flexible search
    @Query("SELECT d FROM DettesFournisseur d LEFT JOIN d.fournisseur f WHERE " +
           "(" +
           "  (:searchTerm IS NULL OR :searchTerm = '' OR " +
           "   LOWER(d.numeroFacture) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "   LOWER(d.titre) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "   LOWER(d.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "   LOWER(f.nom) LIKE LOWER(CONCAT('%', :searchTerm, '%'))" +
           "  )" +
           ") AND " +
           "(:estPaye IS NULL OR d.estPaye = :estPaye) AND " +
           "(:fournisseurId IS NULL OR f.id = :fournisseurId) AND " +
           "(:dateDebut IS NULL OR d.datePaiementPrevue >= :dateDebut) AND " +
           "(:dateFin IS NULL OR d.datePaiementPrevue <= :dateFin)")
    Page<DettesFournisseur> findByFlexibleSearchPaginated(
        @Param("searchTerm") String searchTerm,
        @Param("estPaye") Boolean estPaye,
        @Param("fournisseurId") String fournisseurId,
        @Param("dateDebut") LocalDate dateDebut,
        @Param("dateFin") LocalDate dateFin,
        Pageable pageable
    );
    
    // Improved search: text fields (numeroFacture, titre, fournisseurNom) are OR'd (match any)
    // while other filters are applied with AND. If none of the text fields are provided,
    // the text condition is treated as true (no filtering by text).
    @Query("SELECT DISTINCT d FROM DettesFournisseur d LEFT JOIN FETCH d.fournisseur f WHERE " +
        "( ( (:numeroFacture IS NULL OR :numeroFacture = '') AND (:titre IS NULL OR :titre = '') AND (:fournisseurNom IS NULL OR :fournisseurNom = '') ) OR (" +
        "(:numeroFacture IS NOT NULL AND :numeroFacture <> '' AND LOWER(d.numeroFacture) LIKE LOWER(CONCAT('%', :numeroFacture, '%'))) OR " +
        "(:titre IS NOT NULL AND :titre <> '' AND LOWER(d.titre) LIKE LOWER(CONCAT('%', :titre, '%'))) OR " +
        "(:fournisseurNom IS NOT NULL AND :fournisseurNom <> '' AND LOWER(f.nom) LIKE LOWER(CONCAT('%', :fournisseurNom, '%'))) ) ) AND " +
        "(:estPaye IS NULL OR d.estPaye = :estPaye) AND " +
        "(:datePaiementPrevue IS NULL OR d.datePaiementPrevue = :datePaiementPrevue) AND " +
        "(:datePaiementReelle IS NULL OR d.datePaiementReelle = :datePaiementReelle) AND " +
        "(:fournisseurId IS NULL OR f.id = :fournisseurId)")
    List<DettesFournisseur> findBySearchCriteria(
        @Param("numeroFacture") String numeroFacture,
        @Param("titre") String titre,
        @Param("estPaye") Boolean estPaye,
        @Param("datePaiementPrevue") LocalDate datePaiementPrevue,
        @Param("datePaiementReelle") LocalDate datePaiementReelle,
        @Param("fournisseurId") String fournisseurId,
        @Param("fournisseurNom") String fournisseurNom
    );
    
    @Query("SELECT d FROM DettesFournisseur d LEFT JOIN d.fournisseur f WHERE " +
        "( ( (:numeroFacture IS NULL OR :numeroFacture = '') AND (:titre IS NULL OR :titre = '') AND (:fournisseurNom IS NULL OR :fournisseurNom = '') ) OR (" +
        "(:numeroFacture IS NOT NULL AND :numeroFacture <> '' AND LOWER(d.numeroFacture) LIKE LOWER(CONCAT('%', :numeroFacture, '%'))) OR " +
        "(:titre IS NOT NULL AND :titre <> '' AND LOWER(d.titre) LIKE LOWER(CONCAT('%', :titre, '%'))) OR " +
        "(:fournisseurNom IS NOT NULL AND :fournisseurNom <> '' AND LOWER(f.nom) LIKE LOWER(CONCAT('%', :fournisseurNom, '%'))) ) ) AND " +
        "(:estPaye IS NULL OR d.estPaye = :estPaye) AND " +
        "(:datePaiementPrevue IS NULL OR d.datePaiementPrevue = :datePaiementPrevue) AND " +
        "(:datePaiementReelle IS NULL OR d.datePaiementReelle = :datePaiementReelle) AND " +
        "(:fournisseurId IS NULL OR f.id = :fournisseurId)")
    Page<DettesFournisseur> findBySearchCriteriaPaginated(
        @Param("numeroFacture") String numeroFacture,
        @Param("titre") String titre,
        @Param("estPaye") Boolean estPaye,
        @Param("datePaiementPrevue") LocalDate datePaiementPrevue,
        @Param("datePaiementReelle") LocalDate datePaiementReelle,
        @Param("fournisseurId") String fournisseurId,
        @Param("fournisseurNom") String fournisseurNom,
        Pageable pageable
    );
    
    boolean existsByNumeroFacture(String numeroFacture);
}

