package com.example.BacK.infrastructure.repository.g_Vehicule;

import com.example.BacK.domain.g_Vehicule.Vehicule;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehiculeRepository extends JpaRepository<Vehicule, String> {

    @EntityGraph(attributePaths = {"reparations", "transactions"})
    List<Vehicule> findAll();

}
