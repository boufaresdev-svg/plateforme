package com.example.BacK.infrastructure.repository.g_Formation;

import com.example.BacK.domain.g_Formation.Formation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface FormationRepository extends JpaRepository<Formation, Long> {
    
    @Query("SELECT f FROM Formation f " +
           "LEFT JOIN FETCH f.programmesDetailes " +
           "WHERE f.idFormation = :id")
    Optional<Formation> findByIdWithProgrammes(@Param("id") Long id);
    
    @Query("SELECT f FROM Formation f " +
           "LEFT JOIN FETCH f.objectifsSpecifiques " +
           "WHERE f.idFormation = :id")
    Optional<Formation> findByIdWithObjectifs(@Param("id") Long id);
}
