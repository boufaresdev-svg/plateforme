package com.example.BacK.application.interfaces.g_Formation.Examen;

import com.example.BacK.domain.g_Formation.Examen;

import java.util.List;
import java.util.Optional;

public interface IExamenRepositoryService {

    Examen saveExamen(Examen examen);
    Examen updateExamen(Long id, Examen examen);
    void deleteExamen(Long id);
    Optional<Examen> getExamenById(Long id);
    List<Examen> getAllExamens();
    List<Examen> getExamensByPlanFormation(Long idPlanFormation);
    boolean existsById(Long id);
}


