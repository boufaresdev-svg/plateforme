package com.example.BacK.infrastructure.repository.g_Projet;

import com.example.BacK.domain.g_Projet.LivrableTache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LivrableTacheRepository extends JpaRepository<LivrableTache, String> {

}