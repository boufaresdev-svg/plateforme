package com.example.BacK.application.interfaces.g_Stock.marque;

import com.example.BacK.domain.g_Stock.Marque;

import java.util.List;

public interface IMarqueRepositoryService {
    
    Marque add(Marque marque);
    
    Marque update(Marque marque);
    
    void delete(String id);
    
    Marque getById(String id);
    
    List<Marque> getAll();
    
    List<Marque> getByEstActif(boolean estActif);
    
    List<Marque> search(String nom, String codeMarque, String pays, Boolean estActif);
}
