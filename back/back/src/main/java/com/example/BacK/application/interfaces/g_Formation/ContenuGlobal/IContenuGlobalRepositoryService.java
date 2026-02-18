package com.example.BacK.application.interfaces.g_Formation.ContenuGlobal;

import com.example.BacK.domain.g_Formation.ContenuGlobal;

import java.util.List;
import java.util.Optional;

public interface IContenuGlobalRepositoryService {

    ContenuGlobal saveContenuGlobal(ContenuGlobal contenuGlobal);
    ContenuGlobal updateContenuGlobal(Long id, ContenuGlobal contenuGlobal);
    void deleteContenuGlobal(Long id);
    Optional<ContenuGlobal> getContenuGlobalById(Long id);
    List<ContenuGlobal> getAllContenusGlobaux();
    List<ContenuGlobal> getContenusByFormation(Long idFormation);
    boolean existsById(Long id);

}



