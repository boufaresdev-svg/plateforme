package com.example.BacK.application.g_Projet.Command.Tache.update;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Projet.Charge;
import com.example.BacK.domain.g_Projet.CommentaireTache;
import com.example.BacK.domain.g_Projet.Mission;
import com.example.BacK.domain.g_Projet.Tache;
import com.example.BacK.domain.g_Projet.enumEntity.PrioriteTache;
import com.example.BacK.domain.g_Projet.enumEntity.StatutTache;
import com.example.BacK.domain.g_RH.Employee;
import com.example.BacK.infrastructure.services.g_Projet.MissionRepositoryService;
import com.example.BacK.infrastructure.services.g_Projet.TacheRepositoryService;
import com.example.BacK.infrastructure.services.g_rh.EmployeeRepositoryService;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component("UpdateTacheHandler")
public class UpdateTacheHandler implements RequestHandler<UpdateTacheCommand, Void> {

    private final TacheRepositoryService tacheRepositoryService;
    private final MissionRepositoryService missionRepositoryService;
    private final EmployeeRepositoryService employeeRepositoryService;
    private final ModelMapper modelMapper;

    public UpdateTacheHandler(TacheRepositoryService tacheRepositoryService,
                              MissionRepositoryService missionRepositoryService,
                              EmployeeRepositoryService employeeRepositoryService,
                              ModelMapper modelMapper) {
        this.tacheRepositoryService = tacheRepositoryService;
        this.missionRepositoryService = missionRepositoryService;
        this.employeeRepositoryService = employeeRepositoryService;
        this.modelMapper = modelMapper;
    }

    @Override
    public Void handle(UpdateTacheCommand command) {
        //ache tache = modelMapper.map(command, Tache.class);
        Tache tache = new Tache();
        // Récupération de la mission associée
        Mission missionFound = missionRepositoryService.get(command.getMissionId());
        tache.setMission(missionFound);

        Tache tacheFound = tacheRepositoryService.get(command.getId());

        tache.setId(command.getId());
        tache.setNom(command.getNom());
        tache.setDescription(command.getDescription());
        tache.setPriorite(command.getPriorite());
        tache.setStatut(command.getStatut());
        tache.setDateDebut(command.getDateDebut());
        tache.setDateFin(command.getDateFin());
        tache.setDureeEstimee(command.getDureeEstimee());
        tache.setDureeReelle(command.getDureeReelle());
        tache.setProgression(command.getProgression());
        tache.setEmployee(tacheFound.getEmployee());
        tache.setCommentaires(tacheFound.getCommentaires());
        tache.setCharges(tacheFound.getCharges());

        tacheRepositoryService.update(tache);

        return null;
    }
}