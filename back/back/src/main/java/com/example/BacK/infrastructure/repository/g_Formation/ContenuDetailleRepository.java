package com.example.BacK.infrastructure.repository.g_Formation;

import com.example.BacK.domain.g_Formation.ContenuDetaille;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContenuDetailleRepository extends JpaRepository<ContenuDetaille, Long> {

    /**
     * Find all contenu detaille for a specific jour formation
     */
    List<ContenuDetaille> findByJourFormation_IdJour(Long idJour);

    /**
     * Find paginated contenu detaille for a specific jour formation
     */
    Page<ContenuDetaille> findByJourFormation_IdJour(Long idJour, Pageable pageable);

    /**
     * Find all contenu detaille for a specific programme
     */
    List<ContenuDetaille> findByJourFormation_ProgrammeDetaile_IdProgramme(Long idProgramme);

    /**
     * Find all contenu detaille for a specific formation
     */
    List<ContenuDetaille> findByJourFormation_ProgrammeDetaile_Formation_IdFormation(Long idFormation);

    /**
     * Find all contenu detaille assigned to a formation through ContenuJourNiveauAssignment
     */
    @Query("""
        SELECT DISTINCT cd.idContenuDetaille FROM ContenuDetaille cd
        INNER JOIN cd.jourFormation jf
        INNER JOIN jf.programmeDetaile pd
        INNER JOIN pd.formation f
        WHERE f.idFormation = :idFormation
    """)
    List<Long> findAssignedContenuIdsByFormation(@Param("idFormation") Long idFormation);

    /**
     * Find all contenu detaille by IDs
     */
    @Query("SELECT cd FROM ContenuDetaille cd WHERE cd.idContenuDetaille IN :ids")
    List<ContenuDetaille> findByIds(@Param("ids") List<Long> ids);

    /**
     * Find contenu by titre
     */
    List<ContenuDetaille> findByTitre(String titre);

    /**
     * Search contenu detaille by titre (case-insensitive, substring)
     */
    List<ContenuDetaille> findByTitreContainingIgnoreCase(String titre);
}
