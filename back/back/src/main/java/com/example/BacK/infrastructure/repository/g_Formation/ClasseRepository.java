package com.example.BacK.infrastructure.repository.g_Formation;

import com.example.BacK.domain.g_Formation.Classe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClasseRepository extends JpaRepository<Classe, Long> {

    Optional<Classe> findByCode(String code);

    boolean existsByCode(String code);

    // Find by id with apprenants eagerly fetched
    @Query("SELECT c FROM Classe c LEFT JOIN FETCH c.apprenants LEFT JOIN FETCH c.formation LEFT JOIN FETCH c.planFormation WHERE c.id = :id")
    Optional<Classe> findByIdWithApprenants(@Param("id") Long id);

    // Find all classes for a specific formation
    List<Classe> findByFormation_IdFormation(Long formationId);

    // Find all classes for a specific plan formation
    List<Classe> findByPlanFormation_IdPlanFormation(Long planFormationId);

    // Find active classes
    List<Classe> findByIsActiveTrue();

    // Find classes by formation with active filter
    List<Classe> findByFormation_IdFormationAndIsActive(Long formationId, Boolean isActive);

    // Search classes by name or code
    @Query("SELECT c FROM Classe c WHERE " +
           "(:search IS NULL OR :search = '' OR " +
           "LOWER(c.nom) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.code) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Classe> searchClasses(@Param("search") String search, Pageable pageable);

    // Find classes by formation (either direct or through plan)
    @Query("SELECT c FROM Classe c WHERE " +
           "c.formation.idFormation = :formationId OR " +
           "c.planFormation.formation.idFormation = :formationId")
    List<Classe> findAllByFormationId(@Param("formationId") Long formationId);

    // Count classes by formation
    @Query("SELECT COUNT(c) FROM Classe c WHERE " +
           "c.formation.idFormation = :formationId OR " +
           "c.planFormation.formation.idFormation = :formationId")
    long countByFormationId(@Param("formationId") Long formationId);

    // Count active classes
    long countByIsActiveTrue();

    // Find classes with available spots
    @Query("SELECT c FROM Classe c WHERE " +
           "c.isActive = true AND " +
           "(c.capaciteMax IS NULL OR c.capaciteMax > SIZE(c.apprenants))")
    List<Classe> findClassesWithAvailableSpots();

    // Find classes containing a specific apprenant
    @Query("SELECT c FROM Classe c JOIN c.apprenants a WHERE a.id = :apprenantId")
    List<Classe> findByApprenantId(@Param("apprenantId") Long apprenantId);
}
