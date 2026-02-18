package com.example.BacK.infrastructure.repository.g_Formation;

import com.example.BacK.domain.g_Formation.ContenuJourFormation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContenuJourFormationRepository extends JpaRepository<ContenuJourFormation, Long> {

    List<ContenuJourFormation> findByObjectifSpecifique_IdObjectifSpec(Long idObjectifSpec);

    @Query("SELECT c FROM ContenuJourFormation c " +
           "LEFT JOIN FETCH c.contenuAssignments ca " +
           "WHERE c.idContenuJour = :id")
    Optional<ContenuJourFormation> findByIdWithContenusDetailles(@Param("id") Long id);

    @Query("SELECT c FROM ContenuJourFormation c WHERE c.planFormation.idPlanFormation = :planId ORDER BY c.ordre ASC, c.idContenuJour ASC")
    List<ContenuJourFormation> findByPlanFormationOrderByOrdre(@Param("planId") Long planId);

    @Query("SELECT DISTINCT cjf.idContenuJour FROM ContenuJourFormation cjf")
    List<Long> findAllIds();

}
