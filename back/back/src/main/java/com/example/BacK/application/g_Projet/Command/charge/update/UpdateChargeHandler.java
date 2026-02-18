package com.example.BacK.application.g_Projet.Command.charge.update;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Projet.Charge;
import com.example.BacK.domain.g_RH.Employee;
import com.example.BacK.infrastructure.services.g_Projet.ChargeRepositoryService;
import com.example.BacK.infrastructure.services.g_rh.EmployeeRepositoryService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component("UpdateChargeHandler")
public class UpdateChargeHandler implements RequestHandler<UpdateChargeCommand, Void> {

    private final ChargeRepositoryService chargeRepositoryService;
    private final EmployeeRepositoryService employeeRepositoryService;
    private final ModelMapper modelMapper;

    public UpdateChargeHandler(ChargeRepositoryService chargeRepositoryService,
                               EmployeeRepositoryService employeeRepositoryService,
                               ModelMapper modelMapper) {
        this.chargeRepositoryService = chargeRepositoryService;
        this.employeeRepositoryService = employeeRepositoryService;
        this.modelMapper = modelMapper;
    }

    @Override
    public Void handle(UpdateChargeCommand command) {
        Charge charge = modelMapper.map(command, Charge.class);
        Employee employeFound = employeeRepositoryService.get(command.getEmploye());
        charge.setEmploye(employeFound);
        this.chargeRepositoryService.update(charge);
        return null;
    }
}