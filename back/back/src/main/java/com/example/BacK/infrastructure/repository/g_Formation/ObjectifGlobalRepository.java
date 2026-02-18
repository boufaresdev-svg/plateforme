package com.example.BacK.infrastructure.repository.g_Formation;

import com.example.BacK.domain.g_Formation.ObjectifGlobal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ObjectifGlobalRepository extends JpaRepository<ObjectifGlobal, Long> {

    List<ObjectifGlobal> findByFormations_IdFormation(Long idFormation);

    /**
     * Search global objectives by keyword in libelle, description, or tags
     * Case-insensitive search using LOWER function
     */
    @Query("SELECT og FROM ObjectifGlobal og WHERE " +
           "LOWER(og.libelle) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "(og.description IS NOT NULL AND LOWER(CAST(og.description AS string)) LIKE LOWER(CONCAT('%', :keyword, '%'))) OR " +
           "(og.tags IS NOT NULL AND LOWER(CAST(og.tags AS string)) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<ObjectifGlobal> searchByKeyword(@Param("keyword") String keyword);

    /**
     * Find all objectives NOT already linked to a specific formation
     */
    @Query("SELECT og FROM ObjectifGlobal og WHERE og.idObjectifGlobal NOT IN " +
           "(SELECT og2.idObjectifGlobal FROM ObjectifGlobal og2 JOIN og2.formations f WHERE f.idFormation = :formationId)")
    List<ObjectifGlobal> findNotLinkedToFormation(@Param("formationId") Long formationId);

    /**
     * Search objectives by keyword that are NOT already linked to a formation
     */
    @Query("SELECT og FROM ObjectifGlobal og WHERE " +
           "(LOWER(og.libelle) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "(og.description IS NOT NULL AND LOWER(CAST(og.description AS string)) LIKE LOWER(CONCAT('%', :keyword, '%'))) OR " +
           "(og.tags IS NOT NULL AND LOWER(CAST(og.tags AS string)) LIKE LOWER(CONCAT('%', :keyword, '%')))) AND " +
           "og.idObjectifGlobal NOT IN " +
           "(SELECT og2.idObjectifGlobal FROM ObjectifGlobal og2 JOIN og2.formations f WHERE f.idFormation = :formationId)")
    List<ObjectifGlobal> searchByKeywordNotLinkedToFormation(
            @Param("keyword") String keyword,
            @Param("formationId") Long formationId);

}
