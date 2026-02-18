package com.example.BacK.infrastructure.services.g_Formation;

import com.example.BacK.application.interfaces.g_Formation.Evaluation.IEvaluationRepositoryService;
import com.example.BacK.domain.g_Formation.Evaluation;
import com.example.BacK.infrastructure.repository.g_Formation.EvaluationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EvaluationRepositoryService implements IEvaluationRepositoryService {

    private final EvaluationRepository evaluationRepository;

    public EvaluationRepositoryService(EvaluationRepository evaluationRepository) {
        this.evaluationRepository = evaluationRepository;
    }

    @Override
    public Evaluation saveEvaluation(Evaluation evaluation) {
        return evaluationRepository.save(evaluation);
    }

    @Override
    public Evaluation updateEvaluation(Long id, Evaluation evaluation) {
        return evaluationRepository.findById(id)
                .map(existing -> {
                    existing.setType(evaluation.getType());
                    existing.setDate(evaluation.getDate());
                    existing.setDescription(evaluation.getDescription());
                    existing.setScore(evaluation.getScore());
                    existing.setPlanFormation(evaluation.getPlanFormation());
                    existing.setContenuJourFormation(evaluation.getContenuJourFormation());
                    existing.setApprenant(evaluation.getApprenant());
                    return evaluationRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Évaluation non trouvée avec l'ID : " + id));
    }

    @Override
    public void deleteEvaluation(Long id) {
        if (!evaluationRepository.existsById(id)) {
            throw new RuntimeException("Évaluation non trouvée avec l'ID : " + id);
        }
        evaluationRepository.deleteById(id);
    }

    @Override
    public Optional<Evaluation> getEvaluationById(Long id) {
        return evaluationRepository.findById(id);
    }

    @Override
    public List<Evaluation> getAllEvaluations() {
        return evaluationRepository.findAll();
    }

    @Override
    public List<Evaluation> findByContenuJourFormation(Long idContenuJourFormation) {
        return evaluationRepository.findByContenuJourFormation_IdContenuJour(idContenuJourFormation);
    }

    @Override
    public boolean existsById(Long id) {
        return evaluationRepository.existsById(id);
    }
}
