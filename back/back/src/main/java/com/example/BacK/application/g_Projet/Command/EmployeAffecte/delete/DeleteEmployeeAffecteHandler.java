package com.example.BacK.application.g_Projet.Command.EmployeAffecte.delete;


import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.infrastructure.services.g_Projet.EmployeeAffectRepositoryService;
import org.springframework.stereotype.Component;

@Component("DeleteEmployeeAffecteHandler")
public class DeleteEmployeeAffecteHandler implements RequestHandler<DeleteEmployeeAffecteCommand, Void> {

    private final EmployeeAffectRepositoryService employeeAffectRepositoryService;

    public DeleteEmployeeAffecteHandler(EmployeeAffectRepositoryService employeeAffectRepositoryService) {
        this.employeeAffectRepositoryService = employeeAffectRepositoryService;
    }

    @Override
    public Void handle(DeleteEmployeeAffecteCommand command) {
        employeeAffectRepositoryService.delete(command.getId());
        return null; // retour Void
    }
}
