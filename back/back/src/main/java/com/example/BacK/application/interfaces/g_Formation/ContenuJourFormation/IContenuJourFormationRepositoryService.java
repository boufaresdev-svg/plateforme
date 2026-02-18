package com.example.BacK.application.interfaces.g_Formation.ContenuJourFormation;

import com.example.BacK.domain.g_Formation.ContenuJourFormation;

import java.util.List;
import java.util.Optional;

public interface IContenuJourFormationRepositoryService {

    ContenuJourFormation saveContenuJourFormation(ContenuJourFormation contenuJourFormation);
    ContenuJourFormation updateContenuJourFormation(Long id, ContenuJourFormation contenuJourFormation);
    void deleteContenuJourFormation(Long id);
    Optional<ContenuJourFormation> getContenuJourFormationById(Long id);
    List<ContenuJourFormation> getAllContenusJourFormation();
    List<ContenuJourFormation> findByObjectif(Long idObjectifSpec);
    boolean existsById(Long id);

}
