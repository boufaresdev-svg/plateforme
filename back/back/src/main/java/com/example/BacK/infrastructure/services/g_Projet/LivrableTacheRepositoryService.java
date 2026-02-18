package com.example.BacK.infrastructure.services.g_Projet;

import com.example.BacK.application.g_Projet.Query.LivrableTache.GetLivrableTacheResponse;
import com.example.BacK.application.interfaces.g_Projet.LivrableTache.ILivrableTacheRepositoryService;
import com.example.BacK.domain.g_Projet.LivrableTache;
import com.example.BacK.infrastructure.repository.g_Projet.LivrableTacheRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LivrableTacheRepositoryService implements ILivrableTacheRepositoryService {

    private final LivrableTacheRepository livrableTacheRepository;
    public LivrableTacheRepositoryService(LivrableTacheRepository livrableTacheRepository) {
        this.livrableTacheRepository = livrableTacheRepository;
    }

    @Override
    public String add(LivrableTache livrableTache) {
        livrableTache.setId(null);
        LivrableTache livrableTache1 = livrableTacheRepository.save(livrableTache);
        return livrableTache1.getId();
    }

    @Override
    public void update(LivrableTache livrableTache) {
        if (!livrableTacheRepository.existsById(livrableTache.getId())) {
            throw new IllegalArgumentException("CommentaireTache ID not found");
        }
        livrableTacheRepository.save(livrableTache);
    }

    @Override
    public void delete(String id) {
        livrableTacheRepository.deleteById(id);
    }

    @Override
    public GetLivrableTacheResponse get(String id) {
        return livrableTacheRepository.findById(id)
                .map(p -> new GetLivrableTacheResponse(p.getId(), p.getNom(), p.getDescription()))
                .orElseThrow(() -> new RuntimeException("Livrable non trouvé avec l'id : " + id));
    }

    @Override
    public List<GetLivrableTacheResponse> getall() {
        return livrableTacheRepository.findAll()
                .stream()
                .map(p -> new GetLivrableTacheResponse(p.getId(), p.getNom(), p.getDescription()))
                .toList();
    }
}
