package com.example.BacK.infrastructure.services.g_Projet;

import com.example.BacK.application.g_Projet.Query.ProblemeTache.GetProblemeTacheResponse;
import com.example.BacK.application.interfaces.g_Projet.ProblemeTache.IProblemeTacheRepositoryService;
import com.example.BacK.domain.g_Projet.ProblemeTache;
import com.example.BacK.infrastructure.repository.g_Projet.ProblemeTacheRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProblemeTacheRepositoryService implements IProblemeTacheRepositoryService {

    private final ProblemeTacheRepository problemeTacheRepository;
    public ProblemeTacheRepositoryService(ProblemeTacheRepository problemeTacheRepository) {
        this.problemeTacheRepository = problemeTacheRepository;
    }


    @Override
    public String add(ProblemeTache problemeTache) {
        problemeTache.setId(null);
        ProblemeTache probleme = problemeTacheRepository.save(problemeTache);
        return probleme.getId();
    }

    @Override
    public void update(ProblemeTache problemeTache) {
        if (!problemeTacheRepository.existsById(problemeTache.getId())) {
            throw new IllegalArgumentException("CommentaireTache ID not found");
        }
        problemeTacheRepository.save(problemeTache);

    }

    @Override
    public void delete(String id) {
        problemeTacheRepository.deleteById(id);
    }

    @Override
    public GetProblemeTacheResponse get(String id) {
        return problemeTacheRepository.findById(id)
                .map(p -> new GetProblemeTacheResponse(p.getId(), p.getNom(), p.getDescription()))
                .orElseThrow(() -> new RuntimeException("Problème non trouvé avec l'id : " + id));
    }

    @Override
    public List<GetProblemeTacheResponse> getall() {
        return problemeTacheRepository.findAll()
                .stream()
                .map(p -> new GetProblemeTacheResponse(p.getId(), p.getNom(), p.getDescription()))
                .toList();
    }
}
