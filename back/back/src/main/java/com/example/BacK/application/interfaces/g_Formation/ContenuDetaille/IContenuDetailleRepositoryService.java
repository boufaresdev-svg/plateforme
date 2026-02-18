package com.example.BacK.application.interfaces.g_Formation.ContenuDetaille;

import com.example.BacK.domain.g_Formation.ContenuDetaille;

import java.util.List;
import java.util.Optional;

public interface IContenuDetailleRepositoryService {

    ContenuDetaille saveContenuDetaille(ContenuDetaille contenuDetaille);
    
    ContenuDetaille updateContenuDetaille(Long id, ContenuDetaille contenuDetaille);
    
    void deleteContenuDetaille(Long id);
    
    Optional<ContenuDetaille> getContenuDetailleById(Long id);
    
    List<ContenuDetaille> getAllContenusDetailles();
    
    /**
     * Get all contenu detaille for a specific jour formation
     */
    List<ContenuDetaille> getContenuDetailleByJour(Long idJour);
    
    /**
     * Get all contenu detaille for a specific programme
     */
    List<ContenuDetaille> getContenuDetailleByProgramme(Long idProgramme);
    
    /**
     * Get all contenu detaille for a specific formation
     */
    List<ContenuDetaille> getContenuDetailleByFormation(Long idFormation);
    
    /**
     * Get contenu by titre
     */
    List<ContenuDetaille> getContenuDetailleByTitre(String titre);
    
    boolean existsById(Long id);
}
