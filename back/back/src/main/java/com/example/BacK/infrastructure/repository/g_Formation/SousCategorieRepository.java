package com.example.BacK.infrastructure.repository.g_Formation;

import com.example.BacK.domain.g_Formation.SousCategorie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SousCategorieRepository extends JpaRepository<SousCategorie, Long> {

    List<SousCategorie> findByCategorie_IdCategorie(Long idCategorie);

}
