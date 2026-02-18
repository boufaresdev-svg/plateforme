package com.example.BacK.infrastructure.services.g_Formation;

import com.example.BacK.application.interfaces.g_Formation.Categorie.ICategorieRepositoryService;
import com.example.BacK.domain.g_Formation.Categorie;
import com.example.BacK.infrastructure.repository.g_Formation.CategorieRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CategorieRepositoryService implements ICategorieRepositoryService {

    private final CategorieRepository categorieRepository;

    public CategorieRepositoryService(CategorieRepository categorieRepository) {
        this.categorieRepository = categorieRepository;
    }


    @Override
    public Categorie saveCategorie(Categorie categorie) {
        return categorieRepository.save(categorie);
    }


    @Override
    public Categorie updateCategorie(Long id, Categorie categorie) {
        Categorie existingCategorie = categorieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Catégorie non trouvée avec l'ID : " + id));

        if (categorie.getNom() != null) {
            existingCategorie.setNom(categorie.getNom());
        }
        if (categorie.getDescription() != null) {
            existingCategorie.setDescription(categorie.getDescription());
        }
        if (categorie.getType() != null) {
            existingCategorie.setType(categorie.getType());
        }

        return categorieRepository.save(existingCategorie);
    }


    @Override
    public void deleteCategorie(Long id) {
        if (!categorieRepository.existsById(id)) {
            throw new RuntimeException("Catégorie non trouvée avec l'ID : " + id);
        }
        categorieRepository.deleteById(id);
    }


    @Override
    public Optional<Categorie> getCategorieById(Long id) {
        return categorieRepository.findById(id);
    }



    @Override
    public List<Categorie> getAllCategories() {
        return categorieRepository.findAll();
    }

    @Override
    public List<Categorie> findByType(Long idType) {
        return categorieRepository.findByType_IdType(idType);
    }


    @Override
    public boolean existsById(Long id) {
        return categorieRepository.existsById(id);
    }
}

