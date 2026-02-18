package com.example.BacK.application.g_Projet.Command.Tache.add;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Projet.Mission;
import com.example.BacK.domain.g_Projet.Tache;
import com.example.BacK.infrastructure.services.g_Projet.MissionRepositoryService;
import com.example.BacK.infrastructure.services.g_Projet.TacheRepositoryService;
import com.example.BacK.infrastructure.services.g_rh.EmployeeRepositoryService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component("AddTacheHandler")
public class AddTacheHandler implements RequestHandler<AddTacheCommand, AddTacheResponse> {

    private final TacheRepositoryService tacheRepositoryService;
    private final MissionRepositoryService missionRepositoryService;
    private final EmployeeRepositoryService employeeRepositoryService;
    private final ModelMapper modelMapper;

    public AddTacheHandler(TacheRepositoryService tacheRepositoryService,
                           MissionRepositoryService missionRepositoryService,
                           EmployeeRepositoryService employeeRepositoryService,
                           ModelMapper modelMapper) {
        this.tacheRepositoryService = tacheRepositoryService;
        this.missionRepositoryService = missionRepositoryService;
        this.employeeRepositoryService = employeeRepositoryService;
        this.modelMapper = modelMapper;
    }

    @Override
    public AddTacheResponse handle(AddTacheCommand command) {
        Tache tache = modelMapper.map(command, Tache.class);

        // Récupération de la mission associée
        Mission missionFound = missionRepositoryService.get(command.getMissionId());
        tache.setMission(missionFound);

        /* Si nécessaire, assigner des employés
        if (command.getEmploy() != null) {
            List<Employee> employees = command.getEmployeeIds().stream()
                    .map(employeeRepositoryService::get)
                    .toList();
            tache.setEmployesAffectes(employees);
        }
        */

        // Ajout de la tâche
        String id = tacheRepositoryService.add(tache);
        return new AddTacheResponse(id);
    }
}
