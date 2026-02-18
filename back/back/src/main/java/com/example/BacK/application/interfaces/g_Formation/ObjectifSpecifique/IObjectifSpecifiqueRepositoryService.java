package com.example.BacK.application.interfaces.g_Formation.ObjectifSpecifique;

import com.example.BacK.domain.g_Formation.ObjectifSpecifique;

import java.util.List;
import java.util.Optional;

public interface IObjectifSpecifiqueRepositoryService {

    ObjectifSpecifique saveObjectifSpecifique(ObjectifSpecifique objectifSpecifique);
    ObjectifSpecifique updateObjectifSpecifique(Long id, ObjectifSpecifique objectifSpecifique);
    void deleteObjectifSpecifique(Long id);
    Optional<ObjectifSpecifique> getObjectifSpecifiqueById(Long id);
    List<ObjectifSpecifique> getAllObjectifsSpecifiques();
    List<ObjectifSpecifique> getObjectifsByContenu(Long idContenu);
    boolean existsById(Long id);
}


