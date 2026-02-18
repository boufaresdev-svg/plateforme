package com.example.BacK.application.g_Projet.Command.charge.add;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Projet.Charge;
import com.example.BacK.domain.g_Projet.Tache;
import com.example.BacK.domain.g_RH.Employee;
import com.example.BacK.infrastructure.services.g_Projet.ChargeRepositoryService;
import com.example.BacK.infrastructure.services.g_Projet.TacheRepositoryService;
import com.example.BacK.infrastructure.services.g_rh.EmployeeRepositoryService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;


@Component("AddChargeHandler")
public class AddChargeHandler implements RequestHandler<AddChargeCommand, AddChargeResponse> {

    private final ChargeRepositoryService chargeRepositoryService;
    private final EmployeeRepositoryService employeeRepositoryService;
    private final TacheRepositoryService tacheRepositoryService;
    private final ModelMapper modelMapper;

    public AddChargeHandler(ChargeRepositoryService chargeRepositoryService, EmployeeRepositoryService employeeRepositoryService, TacheRepositoryService tacheRepositoryService, ModelMapper modelMapper) {
        this.chargeRepositoryService = chargeRepositoryService;
        this.employeeRepositoryService = employeeRepositoryService;
        this.tacheRepositoryService = tacheRepositoryService;
        this.modelMapper = modelMapper;
    }

    @Override
    public AddChargeResponse handle(AddChargeCommand command) {
        Charge charge = new Charge();
        charge.setNom(command.getNom());
        charge.setMontant(command.getMontant());
        charge.setDescription(command.getDescription());
        charge.setCategorie(command.getCategorie());
        charge.setSousCategorie(command.getSousCategorie());

        Employee employeFound = employeeRepositoryService.get(command.getEmployeId());
        charge.setEmploye(employeFound);

        Tache tacheFound = tacheRepositoryService.get(command.getTacheId());
        charge.setTache(tacheFound);

        String id = this.chargeRepositoryService.add(charge);
        return new AddChargeResponse(id);
    }
}
