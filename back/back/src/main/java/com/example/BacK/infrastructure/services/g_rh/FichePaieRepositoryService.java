package com.example.BacK.infrastructure.services.g_rh;

import com.example.BacK.application.g_RH.Query.fichePaie.GetFichePaieResponse;
import com.example.BacK.application.interfaces.g_rh.fichePaie.IFichePaieRepositoryService;
import com.example.BacK.domain.g_RH.FichePaie;
import com.example.BacK.infrastructure.repository.g_rh.FichePaieRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FichePaieRepositoryService implements IFichePaieRepositoryService {

    private final FichePaieRepository fichePaieRepository;
    private final ModelMapper mapper;

    public FichePaieRepositoryService(FichePaieRepository fichePaieRepository, ModelMapper mapper) {
        this.fichePaieRepository = fichePaieRepository;
        this.mapper = mapper;
    }

    @Override
    public String add(FichePaie fichePaie) {
        fichePaie.setId(null);
        fichePaieRepository.save(fichePaie);
        return "ok";
    }

    @Override
    public void update(FichePaie fichePaie) {
        if (!fichePaieRepository.existsById(fichePaie.getId())) {
            throw new IllegalArgumentException("Fiche de paie ID not found");
        }
        fichePaieRepository.save(fichePaie);
    }

    @Override
    public void delete(String id) {
        fichePaieRepository.deleteById(id);
    }

    @Override
    public FichePaie get(String id) {
        return fichePaieRepository.findById(id).orElse(null);
    }

    @Override
    public List<GetFichePaieResponse> getall() {
        List<FichePaie> fiches = fichePaieRepository.findAll();
        return fiches.stream()
                .map(f -> mapper.map(f, GetFichePaieResponse.class))
                .toList();
    }


}
