package com.example.BacK.application.interfaces.g_Formation.Apprenant;

import com.example.BacK.domain.g_Formation.Apprenant;

import java.util.List;
import java.util.Optional;

public interface IApprenantRepositoryService {

    Apprenant saveApprenant(Apprenant apprenant);
    Apprenant updateApprenant(Long id, Apprenant apprenant);
    void deleteApprenant(Long id);
    Optional<Apprenant> getApprenantById(Long id);
    List<Apprenant> getAllApprenants();
    List<Apprenant> findByPlanFormationId(Long planFormationId);
    boolean existsById(Long id);
}
