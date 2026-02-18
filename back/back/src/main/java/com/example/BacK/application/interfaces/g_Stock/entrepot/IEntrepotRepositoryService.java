package com.example.BacK.application.interfaces.g_Stock.entrepot;

import com.example.BacK.domain.g_Stock.Entrepot;

import java.util.List;
import java.util.Optional;

public interface IEntrepotRepositoryService {
    
    Entrepot save(Entrepot entrepot);
    
    Entrepot getById(String id);
    
    Optional<Entrepot> findById(String id);
    
    Optional<Entrepot> findByNom(String nom);
    
    List<Entrepot> findAll();
    
    List<Entrepot> findByEstActifTrue();
    
    List<Entrepot> findByEstActifFalse();
    
    List<Entrepot> findByVille(String ville);
    
    List<Entrepot> findByStatut(String statut);
    
    List<Entrepot> search(String searchTerm, String ville, String statut, Boolean estActif);
    
    void deleteById(String id);
    
    boolean existsById(String id);
}
