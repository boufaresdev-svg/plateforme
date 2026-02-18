package com.example.BacK.infrastructure.services.g_rh;

import com.example.BacK.application.g_RH.Query.prime.GetPrimeResponse;
import com.example.BacK.application.interfaces.g_rh.prime.IPrimeRepositoryService;
import com.example.BacK.domain.g_RH.Prime;
import com.example.BacK.infrastructure.repository.g_rh.PrimeRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PrimeRepositoryService implements IPrimeRepositoryService {

    private final PrimeRepository primeRepository;
    private final ModelMapper mapper;

    public PrimeRepositoryService(PrimeRepository primeRepository, ModelMapper mapper) {
        this.primeRepository = primeRepository;
        this.mapper = mapper;
    }

    @Override
    public String add(Prime prime) {
        prime.setId(null);
        primeRepository.save(prime);
        return "ok";
    }

    @Override
    public void update(Prime prime) {
        if (!primeRepository.existsById(prime.getId())) {
            throw new IllegalArgumentException("Prime ID not found");
        }
        primeRepository.save(prime);
    }

    @Override
    public void delete(String id) {
        primeRepository.deleteById(id);
    }

    @Override
    public Prime get(String id) {
        return primeRepository.findById(id).orElse(null);
    }

    @Override
    public List<GetPrimeResponse> getall() {
        List <Prime> primes = primeRepository.findAll();
        return primes.stream()
                .map(p -> mapper.map(p, GetPrimeResponse.class))
                .toList();
    }


}

