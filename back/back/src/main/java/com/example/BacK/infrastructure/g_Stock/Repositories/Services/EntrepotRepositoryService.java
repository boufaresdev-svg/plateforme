package com.example.BacK.infrastructure.g_Stock.Repositories.Services;

import com.example.BacK.application.interfaces.g_Stock.entrepot.IEntrepotRepositoryService;
import com.example.BacK.domain.g_Stock.Entrepot;
import com.example.BacK.infrastructure.g_Stock.Repositories.EntrepotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EntrepotRepositoryService implements IEntrepotRepositoryService {
    
    private final EntrepotRepository entrepotRepository;
    
    @Override
    public Entrepot save(Entrepot entrepot) {
        return entrepotRepository.save(entrepot);
    }
    
    @Override
    public Entrepot getById(String id) {
        return entrepotRepository.findById(id).orElse(null);
    }
    
    @Override
    public Optional<Entrepot> findById(String id) {
        return entrepotRepository.findById(id);
    }
    
    @Override
    public Optional<Entrepot> findByNom(String nom) {
        return entrepotRepository.findByNom(nom);
    }
    
    @Override
    public List<Entrepot> findAll() {
        return entrepotRepository.findAll();
    }
    
    @Override
    public List<Entrepot> findByEstActifTrue() {
        return entrepotRepository.findByEstActifTrue();
    }
    
    @Override
    public List<Entrepot> findByEstActifFalse() {
        return entrepotRepository.findByEstActifFalse();
    }
    
    @Override
    public List<Entrepot> findByVille(String ville) {
        return entrepotRepository.findByVille(ville);
    }
    
    @Override
    public List<Entrepot> findByStatut(String statut) {
        return entrepotRepository.findByStatut(statut);
    }
    
    @Override
    public List<Entrepot> search(String searchTerm, String ville, String statut, Boolean estActif) {
        return entrepotRepository.search(searchTerm, ville, statut, estActif);
    }
    
    @Override
    public void deleteById(String id) {
        entrepotRepository.deleteById(id);
    }
    
    @Override
    public boolean existsById(String id) {
        return entrepotRepository.existsById(id);
    }
}
