package com.example.BacK.infrastructure.services.g_Formation;

import com.example.BacK.application.interfaces.g_Formation.SousCategorie.ISousCategorieService;
import com.example.BacK.domain.g_Formation.SousCategorie;
import com.example.BacK.infrastructure.repository.g_Formation.SousCategorieRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SousCategorieRepositoryService implements ISousCategorieService {

    private final SousCategorieRepository sousCategorieRepository;

    public SousCategorieRepositoryService(SousCategorieRepository sousCategorieRepository) {
        this.sousCategorieRepository = sousCategorieRepository;
    }


    @Override
    public SousCategorie saveSousCategorie(SousCategorie sousCategorie) {
        return sousCategorieRepository.save(sousCategorie);
    }


    @Override
    public SousCategorie updateSousCategorie(Long id, SousCategorie sousCategorie) {
        return sousCategorieRepository.findById(id)
                .map(existing -> {
                    existing.setNom(sousCategorie.getNom());
                    existing.setDescription(sousCategorie.getDescription());
                    existing.setCategorie(sousCategorie.getCategorie());

                    return sousCategorieRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Sous-catégorie non trouvée avec l'ID : " + id));
    }


    @Override
    public void deleteSousCategorie(Long id) {
        if (!sousCategorieRepository.existsById(id)) {
            throw new RuntimeException("Sous-catégorie non trouvée avec l'ID : " + id);
        }
        sousCategorieRepository.deleteById(id);
    }


    @Override
    public Optional<SousCategorie> getSousCategorieById(Long id) {
        return sousCategorieRepository.findById(id);
    }


    @Override
    public List<SousCategorie> getAllSousCategories() {
        return sousCategorieRepository.findAll();
    }

    @Override
    public List<SousCategorie> findByCategorie(Long idCategorie) {
        return sousCategorieRepository.findByCategorie_IdCategorie(idCategorie);
    }


    @Override
    public boolean existsById(Long id) {
        return sousCategorieRepository.existsById(id);
    }
}

