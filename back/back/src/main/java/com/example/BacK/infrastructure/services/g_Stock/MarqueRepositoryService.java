package com.example.BacK.infrastructure.services.g_Stock;

import com.example.BacK.application.interfaces.g_Stock.marque.IMarqueRepositoryService;
import com.example.BacK.domain.g_Stock.Marque;
import com.example.BacK.infrastructure.repository.g_Stock.MarqueRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MarqueRepositoryService implements IMarqueRepositoryService {

    private final MarqueRepository marqueRepository;
    private final ModelMapper modelMapper;

    public MarqueRepositoryService(MarqueRepository marqueRepository, ModelMapper modelMapper) {
        this.marqueRepository = marqueRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public Marque add(Marque marque) {
        return marqueRepository.save(marque);
    }

    @Override
    public Marque update(Marque marque) {
        return marqueRepository.save(marque);
    }

    @Override
    public void delete(String id) {
        marqueRepository.deleteById(id);
    }

    @Override
    public Marque getById(String id) {
        return marqueRepository.findById(id).orElse(null);
    }

    @Override
    public List<Marque> getAll() {
        return marqueRepository.findAll();
    }

    @Override
    public List<Marque> getByEstActif(boolean estActif) {
        return estActif ? marqueRepository.findByEstActifTrue() : marqueRepository.findByEstActifFalse();
    }

    @Override
    public List<Marque> search(String nom, String codeMarque, String pays, Boolean estActif) {
        return marqueRepository.search(nom, codeMarque, pays, estActif);
    }
}
