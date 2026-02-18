package com.example.BacK.infrastructure.repository.g_Vehicule;

import com.example.BacK.domain.g_Vehicule.Reparation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ReparationRepository extends JpaRepository<Reparation, String> {

}