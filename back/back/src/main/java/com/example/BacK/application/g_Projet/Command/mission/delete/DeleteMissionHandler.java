package com.example.BacK.application.g_Projet.Command.mission.delete;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.infrastructure.services.g_Projet.MissionRepositoryService;
import org.springframework.stereotype.Component;

@Component("DeleteMissionHandler")
public class DeleteMissionHandler implements RequestHandler<DeleteMissionCommand, Void> {

    private final MissionRepositoryService missionRepositoryService;

    public DeleteMissionHandler(MissionRepositoryService missionRepositoryService) {
        this.missionRepositoryService = missionRepositoryService;
    }

    @Override
    public Void handle(DeleteMissionCommand command) {
        missionRepositoryService.delete(command.getId());
        return null; // retour Void
    }
}
