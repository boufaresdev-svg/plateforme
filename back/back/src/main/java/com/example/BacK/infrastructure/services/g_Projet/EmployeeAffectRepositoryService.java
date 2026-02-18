package com.example.BacK.infrastructure.services.g_Projet;

import com.example.BacK.application.g_Projet.Query.EmployeAffecte.all.GetEmployeAffecteResponse;
import com.example.BacK.application.interfaces.g_Projet.EmployeeAffecte.IEmployeeAffecteRepositoryService;
import com.example.BacK.domain.g_Projet.EmployeAffecte;
import com.example.BacK.infrastructure.repository.g_Projet.EmployeeAffecteRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeAffectRepositoryService implements IEmployeeAffecteRepositoryService {

    private final EmployeeAffecteRepository employeeAffectRepository;
    private final ModelMapper mapper;

    public EmployeeAffectRepositoryService(EmployeeAffecteRepository employeeAffectRepository, ModelMapper mapper) {
        this.employeeAffectRepository = employeeAffectRepository;
        this.mapper = mapper;
    }

    @Override
    public String add(EmployeAffecte employeeAffect) {
        employeeAffect.setId(null); // ID null pour création
        employeeAffectRepository.save(employeeAffect);
        return "ok";
    }

    @Override
    public void update(EmployeAffecte employeeAffect) {
        if (!employeeAffectRepository.existsById(employeeAffect.getId())) {
            throw new IllegalArgumentException("EmployeeAffect ID not found");
        }
        employeeAffectRepository.save(employeeAffect);
    }

    @Override
    public void delete(String id) {
        employeeAffectRepository.deleteById(id);
    }

    @Override
    public EmployeAffecte get(String id) {
        return employeeAffectRepository.findById(id).orElse(null);
    }

    @Override
    public List<GetEmployeAffecteResponse> getall() {
        List<EmployeAffecte> affects = employeeAffectRepository.findAll();
        return affects.stream()
                .map(e -> mapper.map(e, GetEmployeAffecteResponse.class))
                .toList();
    }
}
