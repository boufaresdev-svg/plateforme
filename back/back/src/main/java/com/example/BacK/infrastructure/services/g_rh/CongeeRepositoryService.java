package com.example.BacK.infrastructure.services.g_rh;

import com.example.BacK.application.g_RH.Query.congee.GetCongeeResponse;
import com.example.BacK.application.interfaces.g_rh.congee.ICongeeRepositoryService;
import com.example.BacK.domain.g_RH.Congee;
import com.example.BacK.infrastructure.repository.g_rh.CongeeRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CongeeRepositoryService implements ICongeeRepositoryService {


    private final CongeeRepository congeeRepository;
    private final ModelMapper mapper;

    public CongeeRepositoryService(CongeeRepository congeeRepository, ModelMapper mapper) {
        this.congeeRepository = congeeRepository;
        this.mapper = mapper;
    }

    @Override
    public String add(Congee congee) {
        congee.setId(null);
        congeeRepository.save(congee);
        return "ok";
    }

    @Override
    public void update(Congee congee) {
        if (!congeeRepository.existsById(congee.getId())) {
            throw new IllegalArgumentException("Conge ID not found");
        }
        congeeRepository.save(congee);
    }

    @Override
    public void delete(String id) {
        congeeRepository.deleteById(id);
    }

    @Override
    public Congee get(String id) {
        return congeeRepository.findById(id).orElse(null);
    }

    @Override
    public List<GetCongeeResponse> getall() {
        List<Congee> congees = congeeRepository.findAll();
        return congees.stream().map(c -> {
            GetCongeeResponse dto = mapper.map(c, GetCongeeResponse.class);

            return dto;
        }).toList();
    }
}
