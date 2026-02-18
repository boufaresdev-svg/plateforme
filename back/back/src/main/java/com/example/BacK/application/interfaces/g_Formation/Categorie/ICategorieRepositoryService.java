package com.example.BacK.application.interfaces.g_Formation.Categorie;


import com.example.BacK.domain.g_Formation.Categorie;

import java.util.List;
import java.util.Optional;

public interface ICategorieRepositoryService {

    Categorie saveCategorie(Categorie categorie);
    Categorie updateCategorie(Long id, Categorie categorie);
    void deleteCategorie(Long id);
    Optional<Categorie> getCategorieById(Long id);
    List<Categorie> getAllCategories();
    List<Categorie> findByType(Long idType);
    boolean existsById(Long id);
}
