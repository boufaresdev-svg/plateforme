package com.example.BacK.infrastructure.services.g_rh;

import com.example.BacK.application.g_RH.Query.employee.GetEmployeeResponse;
import com.example.BacK.application.interfaces.g_rh.employee.IEmployeeRepositoryService;
import com.example.BacK.domain.g_RH.Employee;
import com.example.BacK.infrastructure.repository.g_rh.EmployeeRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeRepositoryService implements IEmployeeRepositoryService {

    private final EmployeeRepository employeeRepository;
    private final ModelMapper mapper;

    public EmployeeRepositoryService(EmployeeRepository employeeRepository, ModelMapper mapper) {
        this.employeeRepository = employeeRepository;
        this.mapper = mapper;
    }

    @Override
    public String add(Employee employee) {
        employee.setId(null);
        employeeRepository.save(employee);
        return "ok";
    }

    @Override
    public void update(Employee employee) {
        if (!employeeRepository.existsById(employee.getId())) {
            throw new IllegalArgumentException("Employee ID not found");
        }
        employeeRepository.save(employee);
    }

    @Override
    public void delete(String id) {
        employeeRepository.deleteById(id);
    }

    @Override
    public Employee get(String id) {
        return employeeRepository.findById(id).orElse(null);
    }
    
    public Employee findByEmail(String email) {
        return employeeRepository.findByEmail(email).orElse(null);
    }

    @Override
    public List<GetEmployeeResponse> getall() {
        List<Employee> employees = employeeRepository.findAll();
        return employees.stream().map(e -> mapper.map(e, GetEmployeeResponse.class)).toList();
    }
}