package com.example.BacK.application.g_Projet.Command.phase.update;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Projet.Mission;
import com.example.BacK.domain.g_Projet.Phase;
import com.example.BacK.domain.g_Projet.Projet;
import com.example.BacK.infrastructure.services.g_Projet.PhaseRepositoryService;
import com.example.BacK.infrastructure.services.g_Projet.ProjectRepositoryService;
import jakarta.persistence.CascadeType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component("UpdatePhaseHandler")
public class UpdatePhaseHandler implements RequestHandler<UpdatePhaseCommand, Void> {

    private final PhaseRepositoryService phaseRepositoryService;
    private final ProjectRepositoryService projectRepositoryService;
    private final ModelMapper modelMapper;

    public UpdatePhaseHandler(PhaseRepositoryService phaseRepositoryService,
                              ProjectRepositoryService projectRepositoryService,
                              ModelMapper modelMapper) {
        this.phaseRepositoryService = phaseRepositoryService;
        this.projectRepositoryService = projectRepositoryService;
        this.modelMapper = modelMapper;
    }

    @Override
    public Void handle(UpdatePhaseCommand command) {
        Phase phase = new Phase();
        Phase phasefound = phaseRepositoryService.get(command.getId());
        Projet projectFound = projectRepositoryService.get(command.getProjet());

        phase.setProjet(projectFound);
        phase.setProgression(phasefound.getProgression());
        phase.setMissions(phasefound.getMissions());

        phase.setId(command.getId());
        phase.setNom(command.getNom());
        phase.setDescription(command.getDescription());
        phase.setOrdre(command.getOrdre());
        phase.setDateDebut(command.getDateDebut());
        phase.setDateFin(command.getDateFin());
        phase.setBudget(command.getBudget());
        phase.setStatut(command.getStatut());

        phaseRepositoryService.update(phase);

        return null; // retour Void
    }
}
