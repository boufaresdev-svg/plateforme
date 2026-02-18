package com.example.BacK.infrastructure.repository.g_Projet;

import com.example.BacK.domain.g_Projet.EmployeAffecte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeAffecteRepository extends JpaRepository<EmployeAffecte, String> {

}