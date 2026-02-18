package com.example.BacK.application.g_RH.Command.employee.addEmployee;


import com.example.BacK.application.mediator.RequestHandler;

import com.example.BacK.domain.g_RH.Employee;
import com.example.BacK.infrastructure.services.g_rh.EmployeeRepositoryService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component("AddEmployeeHandler")
public class AddEmployeeHandler implements RequestHandler<AddEmployeeCommand, AddEmployeeResponse> {

    private final ModelMapper modelMapper;
    private final EmployeeRepositoryService employeeRepositoryService;

    public AddEmployeeHandler(ModelMapper modelMapper, EmployeeRepositoryService employeeRepositoryService) {
        this.modelMapper = modelMapper;
        this.employeeRepositoryService = employeeRepositoryService;
    }

    @Override
    public AddEmployeeResponse handle(AddEmployeeCommand command) {
        Employee employee = modelMapper.map(command, Employee.class);
        String id = this.employeeRepositoryService.add(employee);
        return new AddEmployeeResponse(id);
    }
}