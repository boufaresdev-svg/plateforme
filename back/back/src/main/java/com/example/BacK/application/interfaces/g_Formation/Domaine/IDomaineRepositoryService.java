package com.example.BacK.application.interfaces.g_Formation.Domaine;


import com.example.BacK.domain.g_Formation.Domaine;
import java.util.List;
import java.util.Optional;

public interface IDomaineRepositoryService {

    Domaine saveDomaine(Domaine domaine);
    Domaine updateDomaine(Long id, Domaine domaine);
    void deleteDomaine(Long id);
    Optional<Domaine> getDomaineById(Long id);
    List<Domaine> getAllDomaines();
}
