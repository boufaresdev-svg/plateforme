package com.example.BacK.application.g_Projet.Command.mission.add;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Projet.Mission;
import com.example.BacK.domain.g_Projet.Phase;
import com.example.BacK.domain.g_Projet.Projet;
import com.example.BacK.domain.g_RH.Employee;
import com.example.BacK.infrastructure.services.g_Projet.MissionRepositoryService;
import com.example.BacK.infrastructure.services.g_Projet.PhaseRepositoryService;
import com.example.BacK.infrastructure.services.g_Projet.ProjectRepositoryService;
import com.example.BacK.infrastructure.services.g_rh.EmployeeRepositoryService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("AddMissionHandler")
public class AddMissionHandler implements RequestHandler<AddMissionCommand, AddMissionResponse> {

    private final MissionRepositoryService missionRepositoryService;
    private final ProjectRepositoryService projectRepositoryService;
     private final PhaseRepositoryService phaseRepositoryService;
    private final ModelMapper modelMapper;

    public AddMissionHandler(MissionRepositoryService missionRepositoryService, ProjectRepositoryService projectRepositoryService,   PhaseRepositoryService phaseRepositoryService, ModelMapper modelMapper) {
        this.missionRepositoryService = missionRepositoryService;
        this.projectRepositoryService = projectRepositoryService;
         this.phaseRepositoryService = phaseRepositoryService;
        this.modelMapper = modelMapper;
    }

    @Override
    public AddMissionResponse handle(AddMissionCommand command) {
        Mission mission = modelMapper.map(command, Mission.class);


        Projet projectFound = projectRepositoryService.get(command.getProjet());
        mission.setProjet(projectFound);

        Phase phaseFound = phaseRepositoryService.get(command.getPhase());
        mission.setPhase(phaseFound);

        // Ajout de la mission
        String id = missionRepositoryService.add(mission);
        return new AddMissionResponse(id);
    }
}
