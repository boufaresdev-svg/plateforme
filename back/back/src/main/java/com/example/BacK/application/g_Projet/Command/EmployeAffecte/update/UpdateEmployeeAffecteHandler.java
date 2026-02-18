package com.example.BacK.application.g_Projet.Command.EmployeAffecte.update;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Projet.EmployeAffecte;
import com.example.BacK.domain.g_Projet.Projet;
import com.example.BacK.domain.g_RH.Employee;
import com.example.BacK.infrastructure.services.g_Projet.EmployeeAffectRepositoryService;
import com.example.BacK.infrastructure.services.g_Projet.ProjectRepositoryService;
import com.example.BacK.infrastructure.services.g_rh.EmployeeRepositoryService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component("UpdateEmployeeAffecteHandler")
public class UpdateEmployeeAffecteHandler implements RequestHandler<UpdateEmployeeAffecteCommand, Void> {

    private final EmployeeAffectRepositoryService employeeAffectRepositoryService;
    private final EmployeeRepositoryService employeeRepositoryService;
    private final ProjectRepositoryService projectRepositoryService;
    private final ModelMapper modelMapper;

    public UpdateEmployeeAffecteHandler(EmployeeAffectRepositoryService employeeAffectRepositoryService,
                                        EmployeeRepositoryService employeeRepositoryService,
                                        ProjectRepositoryService projectRepositoryService,
                                        ModelMapper modelMapper) {
        this.employeeAffectRepositoryService = employeeAffectRepositoryService;
        this.employeeRepositoryService = employeeRepositoryService;
        this.projectRepositoryService = projectRepositoryService;
        this.modelMapper = modelMapper;
    }

    @Override
    public Void handle(UpdateEmployeeAffecteCommand command) {
        EmployeAffecte employeeAffecte = modelMapper.map(command, EmployeAffecte.class);

        // Mise à jour de l'affectation
        employeeAffectRepositoryService.update(employeeAffecte);

        return null; // retour Void
    }
}
