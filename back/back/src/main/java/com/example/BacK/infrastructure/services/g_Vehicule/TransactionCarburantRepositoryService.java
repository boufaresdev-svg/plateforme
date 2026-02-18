package com.example.BacK.infrastructure.services.g_Vehicule;


import com.example.BacK.application.g_Vehicule.Query.TransactionCarburantResponse.GetTransactionCarburantResponse;
import com.example.BacK.application.interfaces.g_Vehicule.transactionCarburant.ITransactionCarburantRepositoryService;
import com.example.BacK.domain.g_Vehicule.TransactionCarburant;
import com.example.BacK.infrastructure.repository.g_Vehicule.TransactionCarburantRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionCarburantRepositoryService implements ITransactionCarburantRepositoryService {
    private final ModelMapper mapper;
    private final TransactionCarburantRepository repo;

    public TransactionCarburantRepositoryService(ModelMapper mapper, TransactionCarburantRepository repo) {
        this.mapper = mapper;
        this.repo = repo;
    }

    @Override
    public String add(TransactionCarburant transaction) {
        repo.save(transaction);
        return "ok";
    }

    @Override
    public void update(TransactionCarburant transaction) {
        if (!repo.existsById(transaction.getId())) {
            throw new IllegalArgumentException("Transaction ID not found");
        }
        repo.save(transaction);
    }

    @Override
    public void delete(String id) {
        repo.deleteById(id);
    }

    @Override
    public TransactionCarburant get(String id) {
        return repo.findById(id).orElse(null);
    }

    @Override
    public List<GetTransactionCarburantResponse> filtre(TransactionCarburant filter) {
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreNullValues()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        Example<TransactionCarburant> example = Example.of(filter, matcher);
        return repo.findAll(example).stream()
                .map(t -> mapper.map(t, GetTransactionCarburantResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<GetTransactionCarburantResponse> getall() {
        return repo.findAll().stream().map(t -> mapper.map(t, GetTransactionCarburantResponse.class)).collect(Collectors.toList());
    }
}

