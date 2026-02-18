package com.example.BacK.application.g_Projet.Command.phase.add;


import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Projet.Phase;
import com.example.BacK.domain.g_Projet.Projet;
import com.example.BacK.infrastructure.services.g_Projet.PhaseRepositoryService;
import com.example.BacK.infrastructure.services.g_Projet.ProjectRepositoryService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component("AddPhaseHandler")
public class AddPhaseHandler implements RequestHandler<AddPhaseCommand, AddPhaseResponse> {

    private final PhaseRepositoryService phaseRepositoryService;
    private final ProjectRepositoryService projectRepositoryService;
    private final ModelMapper modelMapper;

    public AddPhaseHandler(PhaseRepositoryService phaseRepositoryService,
                           ProjectRepositoryService projectRepositoryService,
                           ModelMapper modelMapper) {
        this.phaseRepositoryService = phaseRepositoryService;
        this.projectRepositoryService = projectRepositoryService;
        this.modelMapper = modelMapper;
    }

    @Override
    public AddPhaseResponse handle(AddPhaseCommand command) {
        Phase phase = modelMapper.map(command, Phase.class);

        // Récupération du projet associé
        Projet projectFound = projectRepositoryService.get(command.getProjet());
        phase.setProjet(projectFound);

        // Ajout de la phase
        String id = phaseRepositoryService.add(phase);
        return new AddPhaseResponse(id);
    }
}
