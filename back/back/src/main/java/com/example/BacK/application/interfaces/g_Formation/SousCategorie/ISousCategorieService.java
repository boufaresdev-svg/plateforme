package com.example.BacK.application.interfaces.g_Formation.SousCategorie;


import com.example.BacK.domain.g_Formation.SousCategorie;

import java.util.List;
import java.util.Optional;

public interface ISousCategorieService {

    SousCategorie saveSousCategorie(SousCategorie sousCategorie);
    SousCategorie updateSousCategorie(Long id, SousCategorie sousCategorie);
    void deleteSousCategorie(Long id);
    Optional<SousCategorie> getSousCategorieById(Long id);
    List<SousCategorie> getAllSousCategories();
    List<SousCategorie> findByCategorie(Long idCategorie);
    boolean existsById(Long id);
}
