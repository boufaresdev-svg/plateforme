package com.example.BacK.application.g_RH.Command.employee.deleteEmployee;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.infrastructure.services.g_rh.EmployeeRepositoryService;
import org.springframework.stereotype.Component;


@Component("DeleteEmployeeHandler")
public class DeleteEmployeeHandler implements RequestHandler<DeleteEmployeeCommand, Void> {

   private EmployeeRepositoryService   employeeRepositoryService;

    public DeleteEmployeeHandler(EmployeeRepositoryService employeeRepositoryService) {
        this.employeeRepositoryService = employeeRepositoryService;
    }

    @Override
    public Void handle(DeleteEmployeeCommand command) {
        this.employeeRepositoryService.delete(command.getId());
        return null;
    }
}