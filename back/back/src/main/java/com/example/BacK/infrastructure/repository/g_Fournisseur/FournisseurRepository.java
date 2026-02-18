package com.example.BacK.infrastructure.repository.g_Fournisseur;

import com.example.BacK.domain.g_Fournisseur.Fournisseur;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FournisseurRepository extends JpaRepository<Fournisseur, String> {
    
    @Query("SELECT DISTINCT f FROM Fournisseur f LEFT JOIN FETCH f.dettes")
    List<Fournisseur> findAllWithDettes();
    
    @Query("SELECT f FROM Fournisseur f")
    Page<Fournisseur> findAllPaginated(Pageable pageable);
    
    @Query("SELECT DISTINCT f FROM Fournisseur f LEFT JOIN FETCH f.dettes WHERE f.id = :id")
    Optional<Fournisseur> findByIdWithDettes(@Param("id") String id);
    
    @Query("SELECT DISTINCT f FROM Fournisseur f LEFT JOIN FETCH f.dettes WHERE " +
           "(:nom IS NOT NULL AND LOWER(f.nom) LIKE LOWER(CONCAT('%', :nom, '%'))) OR " +
           "(:infoContact IS NOT NULL AND LOWER(f.infoContact) LIKE LOWER(CONCAT('%', :infoContact, '%'))) OR " +
           "(:adresse IS NOT NULL AND LOWER(f.adresse) LIKE LOWER(CONCAT('%', :adresse, '%'))) OR " +
           "(:telephone IS NOT NULL AND f.telephone LIKE CONCAT('%', :telephone, '%')) OR " +
           "(:matriculeFiscale IS NOT NULL AND f.matriculeFiscale = :matriculeFiscale)")
    List<Fournisseur> findBySearchCriteria(
        @Param("nom") String nom,
        @Param("infoContact") String infoContact,
        @Param("adresse") String adresse,
        @Param("telephone") String telephone,
        @Param("matriculeFiscale") String matriculeFiscale,
        @Param("totalAchat") Double totalAchat
    );
    
    @Query("SELECT f FROM Fournisseur f WHERE " +
           "(:nom IS NOT NULL AND LOWER(f.nom) LIKE LOWER(CONCAT('%', :nom, '%'))) OR " +
           "(:infoContact IS NOT NULL AND LOWER(f.infoContact) LIKE LOWER(CONCAT('%', :infoContact, '%'))) OR " +
           "(:adresse IS NOT NULL AND LOWER(f.adresse) LIKE LOWER(CONCAT('%', :adresse, '%'))) OR " +
           "(:telephone IS NOT NULL AND f.telephone LIKE CONCAT('%', :telephone, '%')) OR " +
           "(:matriculeFiscale IS NOT NULL AND f.matriculeFiscale = :matriculeFiscale)")
    Page<Fournisseur> findBySearchCriteriaPaginated(
        @Param("nom") String nom,
        @Param("infoContact") String infoContact,
        @Param("adresse") String adresse,
        @Param("telephone") String telephone,
        @Param("matriculeFiscale") String matriculeFiscale,
        @Param("totalAchat") Double totalAchat,
        Pageable pageable
    );
    
    boolean existsByNom(String nom);
    
    boolean existsByNomAndIdNot(String nom, String id);
}