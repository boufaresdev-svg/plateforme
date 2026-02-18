package com.example.BacK.application.interfaces.g_Formation.Formation;


import com.example.BacK.domain.g_Formation.Formation;

import java.util.List;
import java.util.Optional;

public interface IFormationRepositoryService {

    Formation saveFormation(Formation formation);
    Formation updateFormation(Long id, Formation formation);
    void deleteFormation(Long id);
    Optional<Formation> getFormationById(Long id);
    List<Formation> getAllFormations();
    boolean existsById(Long id);
}
