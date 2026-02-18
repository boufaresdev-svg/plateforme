package com.example.BacK.application.g_Projet.Command.mission.update;

import com.example.BacK.application.g_Projet.Command.mission.add.AddMissionResponse;
import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.application.models.g_projet.EmployeAffecteDTO;
import com.example.BacK.application.models.g_projet.TacheDTO;
import com.example.BacK.domain.g_Projet.Mission;
import com.example.BacK.domain.g_Projet.Phase;
import com.example.BacK.domain.g_Projet.Projet;
import com.example.BacK.domain.g_Projet.enumEntity.PrioriteMission;
import com.example.BacK.domain.g_Projet.enumEntity.StatutMission;
import com.example.BacK.infrastructure.services.g_Projet.MissionRepositoryService;
import com.example.BacK.infrastructure.services.g_Projet.PhaseRepositoryService;
import com.example.BacK.infrastructure.services.g_Projet.ProjectRepositoryService;
import com.example.BacK.infrastructure.services.g_rh.EmployeeRepositoryService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component("UpdateMissionHandler")
public class UpdateMissionHandler implements RequestHandler<UpdateMissionCommand, Void> {

    private final MissionRepositoryService missionRepositoryService;
    private final ProjectRepositoryService projectRepositoryService;
    private final PhaseRepositoryService phaseRepositoryService;
    private final ModelMapper modelMapper;

    public UpdateMissionHandler(MissionRepositoryService missionRepositoryService, ProjectRepositoryService projectRepositoryService, PhaseRepositoryService phaseRepositoryService, ModelMapper modelMapper) {
        this.missionRepositoryService = missionRepositoryService;
        this.projectRepositoryService = projectRepositoryService;
        this.phaseRepositoryService = phaseRepositoryService;
        this.modelMapper = modelMapper;
    }

    @Override
    public Void handle(UpdateMissionCommand command) {

        Mission mission = new Mission();
        Mission missionFound = missionRepositoryService.get(command.getId());
        Projet projectFound = projectRepositoryService.get(command.getProjet());
        mission.setProjet(projectFound);

        Phase phaseFound = phaseRepositoryService.get(command.getPhase());
        mission.setPhase(phaseFound);
        mission.setId(command.getId());
        mission.setNom(command.getNom());
        mission.setDescription(command.getDescription());
        mission.setObjectif(command.getObjectif());
        mission.setStatut(command.getStatut());
        mission.setPriorite(command.getPriorite());
        mission.setDateDebut(command.getDateDebut());
        mission.setDateFin(command.getDateFin());
        mission.setBudget(command.getBudget());

        mission.setTaches(missionFound.getTaches());
        mission.setEmployesAffectes(missionFound.getEmployesAffectes());
        mission.setProgression(missionFound.getProgression());

        missionRepositoryService.update(mission);
        return  null ;
    }
}

