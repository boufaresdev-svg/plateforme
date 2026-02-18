package com.example.BacK.application.g_RH.Command.employee.updateEmployee;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_RH.Employee;
import com.example.BacK.infrastructure.services.g_rh.EmployeeRepositoryService;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;


@Component("UpdateEmployeeHandler")
public class UpdateEmployeeHandler implements RequestHandler<UpdateEmployeeCommand, Void> {

    private final EmployeeRepositoryService employeeRepositoryService;
    private final ModelMapper modelMapper;

    public UpdateEmployeeHandler(EmployeeRepositoryService employeeRepositoryService, ModelMapper modelMapper) {
        this.employeeRepositoryService = employeeRepositoryService;
        this.modelMapper = modelMapper;
    }

    @Override
    public Void handle(UpdateEmployeeCommand command) {
        Employee existingEntity = this.employeeRepositoryService.get(command.getId());
        if (existingEntity == null) {
            throw new EntityNotFoundException("Entity Employee not found");
        }
        this.modelMapper.map(command, existingEntity);
        this.employeeRepositoryService.update(existingEntity);
        return null;
    }
}