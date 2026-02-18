package com.example.BacK.infrastructure.services.g_rh;

import com.example.BacK.application.g_RH.Query.regle.GetRegleResponse;
import com.example.BacK.application.interfaces.g_rh.regle.IRegleRepositoryService;
import com.example.BacK.domain.g_RH.Regle;
import com.example.BacK.infrastructure.repository.g_rh.RegleRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RegleRepositoryService implements IRegleRepositoryService {

    private final RegleRepository regleRepository;
    private final ModelMapper mapper;

    public RegleRepositoryService(RegleRepository regleRepository, ModelMapper mapper) {
        this.regleRepository = regleRepository;
        this.mapper = mapper;
    }

    @Override
    public String add(Regle regle) {
        regle.setId(null);
        regleRepository.save(regle);
        return "ok";
    }

    @Override
    public void update(Regle regle) {
        if (!regleRepository.existsById(regle.getId())) {
            throw new IllegalArgumentException("Regle ID not found");
        }
        regleRepository.save(regle);
    }

    @Override
    public void delete(String id) {
        regleRepository.deleteById(id);
    }

    @Override
    public Regle get(String id) {
        return regleRepository.findById(id).orElse(null);
    }

    @Override
    public List<GetRegleResponse> getall() {
        List<Regle> regles = regleRepository.findAll();
        return regles.stream()
                .map(r -> mapper.map(r, GetRegleResponse.class))
                .toList();
    }


}
