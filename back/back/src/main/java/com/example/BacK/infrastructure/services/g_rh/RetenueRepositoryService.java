package com.example.BacK.infrastructure.services.g_rh;

import com.example.BacK.application.g_RH.Query.retenue.GetReteuneResponse;
import com.example.BacK.application.interfaces.g_rh.retenue.IRetenueRepositoryService;
import com.example.BacK.domain.g_RH.Retenue;
import com.example.BacK.infrastructure.repository.g_rh.RetenueRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RetenueRepositoryService implements IRetenueRepositoryService {

    private final RetenueRepository retenueRepository;
    private final ModelMapper mapper;

    public RetenueRepositoryService(RetenueRepository retenueRepository, ModelMapper mapper) {
        this.retenueRepository = retenueRepository;
        this.mapper = mapper;
    }

    @Override
    public String add(Retenue retenue) {
        retenue.setId(null);
        retenueRepository.save(retenue);
        return "ok";
    }

    @Override
    public void update(Retenue retenue) {
        if (!retenueRepository.existsById(retenue.getId())) {
            throw new IllegalArgumentException("Retenue ID not found");
        }
        retenueRepository.save(retenue);
    }

    @Override
    public void delete(String id) {
        retenueRepository.deleteById(id);
    }

    @Override
    public Retenue get(String id) {
        return retenueRepository.findById(id).orElse(null);
    }

    @Override
    public List<GetReteuneResponse> getall() {
        List<Retenue> retenues = retenueRepository.findAll();
        return retenues.stream()
                .map(r -> mapper.map(r, GetReteuneResponse.class))
                .toList();
    }


}

