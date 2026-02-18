package com.example.BacK.application.interfaces.g_Formation.Evaluation;

import com.example.BacK.domain.g_Formation.Evaluation;

import java.util.List;
import java.util.Optional;

public interface IEvaluationRepositoryService {

    Evaluation saveEvaluation(Evaluation evaluation);
    Evaluation updateEvaluation(Long id, Evaluation evaluation);
    void deleteEvaluation(Long id);
    Optional<Evaluation> getEvaluationById(Long id);
    List<Evaluation> getAllEvaluations();
    List<Evaluation> findByContenuJourFormation(Long idContenuJourFormation);
    boolean existsById(Long id);



}
