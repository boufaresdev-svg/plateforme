package com.example.BacK.infrastructure.services.g_Vehicule;


 import com.example.BacK.application.g_Vehicule.Query.Reparation.GetReparationResponse;
 import com.example.BacK.application.interfaces.g_Vehicule.Reparation.IReparationRepositoryService;
 import com.example.BacK.domain.g_Vehicule.Reparation;
 import com.example.BacK.infrastructure.repository.g_Vehicule.ReparationRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReparationRepositoryService implements IReparationRepositoryService {

    private final ModelMapper _modelMapper;
    private final ReparationRepository _reparationRepository;

    public ReparationRepositoryService(ModelMapper _modelMapper, ReparationRepository _reparationRepository) {
        this._modelMapper = _modelMapper;
        this._reparationRepository = _reparationRepository;
    }

    @Override
    public String add(Reparation reparation) {
        _reparationRepository.save(reparation);
        return "ok";
    }

    @Override
    public void update(Reparation reparation) {
        if (!_reparationRepository.existsById(reparation.getId())) {
            throw new IllegalArgumentException("ID Reparation not found");
        }
        _reparationRepository.save(reparation);
    }

    @Override
    public void delete(String id) {
        _reparationRepository.deleteById(id);
    }

    @Override
    public Reparation get(String id) {
        return _reparationRepository.findById(id).orElse(null);
    }

    @Override
    public List<GetReparationResponse> filtre(Reparation filter) {
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreNullValues()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        Example<Reparation> example = Example.of(filter, matcher);
        return _reparationRepository.findAll(example)
                .stream()
                .map(rep -> _modelMapper.map(rep, GetReparationResponse.class))
                .collect(Collectors.toList());
    }
}

