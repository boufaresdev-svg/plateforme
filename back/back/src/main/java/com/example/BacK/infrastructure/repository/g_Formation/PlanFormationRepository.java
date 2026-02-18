package com.example.BacK.infrastructure.repository.g_Formation;

import com.example.BacK.domain.g_Formation.PlanFormation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanFormationRepository extends JpaRepository<PlanFormation,Long> {

    List<PlanFormation> findByFormation_IdFormation(Long idFormation);





}
