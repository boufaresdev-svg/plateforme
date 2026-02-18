package com.example.BacK.infrastructure.repository.g_Formation;

import com.example.BacK.domain.g_Formation.ContenuJourNiveauAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContenuJourNiveauAssignmentRepository extends JpaRepository<ContenuJourNiveauAssignment, Long> {

    /**
     * Find all assignments for a specific contenu jour
     */
    List<ContenuJourNiveauAssignment> findByContenuJour_IdContenuJour(Long idContenuJour);

    /**
     * Find all assignments for a specific contenu jour and niveau
     */
    List<ContenuJourNiveauAssignment> findByContenuJour_IdContenuJourAndNiveau(Long idContenuJour, Integer niveau);

    /**
     * Find a specific assignment
     */
    Optional<ContenuJourNiveauAssignment> findByContenuJour_IdContenuJourAndContenuDetaille_IdContenuDetailleAndNiveau(
        Long idContenuJour, Long idContenuDetaille, Integer niveau
    );

    /**
     * Find all assignments for a specific contenu detaille
     */
    List<ContenuJourNiveauAssignment> findByContenuDetaille_IdContenuDetaille(Long idContenuDetaille);

    /**
     * Delete assignments by contenu jour id
     */
    void deleteByContenuJour_IdContenuJour(Long idContenuJour);

    /**
     * Delete assignments by contenu detaille id
     */
    void deleteByContenuDetaille_IdContenuDetaille(Long idContenuDetaille);
}
