package com.example.BacK.infrastructure.repository.g_Vehicule;

import com.example.BacK.domain.g_Vehicule.CarteGazoil;
import com.example.BacK.domain.g_Vehicule.Vehicule;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarteGazoleRepository extends JpaRepository<CarteGazoil, String> {

    @EntityGraph(attributePaths = { "transactions"})
    List<CarteGazoil> findAll();
}