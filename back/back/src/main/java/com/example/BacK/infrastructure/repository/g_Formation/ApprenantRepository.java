package com.example.BacK.infrastructure.repository.g_Formation;

import com.example.BacK.domain.g_Formation.Apprenant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface ApprenantRepository extends JpaRepository<Apprenant, Long> {

    List<Apprenant> findByPlanFormation_IdPlanFormation(Long idPlanFormation);
    
    Optional<Apprenant> findByEmail(String email);
    
    Optional<Apprenant> findByMatricule(String matricule);
    
    boolean existsByEmail(String email);
    
    boolean existsByMatricule(String matricule);
    
    @Query("SELECT a FROM Apprenant a WHERE " +
           "(:search IS NULL OR :search = '' OR " +
           "LOWER(a.nom) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(a.prenom) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(a.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(a.matricule) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Apprenant> searchApprenants(@Param("search") String search, Pageable pageable);
    
    @Query("SELECT a FROM Apprenant a WHERE a.planFormation.formation.idFormation = :formationId")
    List<Apprenant> findByFormationId(@Param("formationId") Long formationId);
    
    @Query("SELECT a FROM Apprenant a WHERE a.planFormation.formation.idFormation = :formationId")
    Page<Apprenant> findByFormationIdPaged(@Param("formationId") Long formationId, Pageable pageable);
    
    @Query("SELECT a FROM Apprenant a WHERE a.isBlocked = true")
    List<Apprenant> findBlockedApprenants();
    
    @Query("SELECT a FROM Apprenant a WHERE a.isStaff = true")
    List<Apprenant> findStaffApprenants();
    
    @Query("SELECT COUNT(a) FROM Apprenant a WHERE a.isActive = true")
    long countActiveApprenants();
    
    @Query("SELECT COUNT(a) FROM Apprenant a WHERE a.isBlocked = true")
    long countBlockedApprenants();
}
