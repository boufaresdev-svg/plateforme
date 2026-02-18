package com.example.BacK.infrastructure.repository.g_Projet;

import com.example.BacK.domain.g_Projet.Phase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhaseRepository extends JpaRepository<Phase, String> {

}