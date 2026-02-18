package com.example.BacK.application.g_Projet.Command.projet.update;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Client.Client;
import com.example.BacK.domain.g_Projet.Projet;
import com.example.BacK.infrastructure.services.g_Client.ClientRepositoryService;
import com.example.BacK.infrastructure.services.g_Projet.ProjectRepositoryService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component("UpdateProjetHandler")
public class UpdateProjetHandler implements RequestHandler<UpdateProjetCommand, Void> {

    private final ProjectRepositoryService projectRepositoryService;
    private final ModelMapper modelMapper;
    private final ClientRepositoryService clientRepositoryService;


    public UpdateProjetHandler(ProjectRepositoryService projectRepositoryService, ModelMapper modelMapper, ClientRepositoryService clientRepositoryService) {
        this.projectRepositoryService = projectRepositoryService;
        this.modelMapper = modelMapper;
        this.clientRepositoryService = clientRepositoryService;
    }

    @Override
    public Void handle(UpdateProjetCommand command) {
        Projet project = new Projet();

        Client foundClient = clientRepositoryService.getByid(command.getClient());
        if (foundClient == null) {
            throw new RuntimeException("Client introuvable avec l'id : " + command.getClient());
        }
        Projet projetFound = projectRepositoryService.get(command.getId());
        if (projetFound == null) {
            throw new RuntimeException("Projet introuvable avec l'id : " + command.getId());
        }
        project.setPhases(projetFound.getPhases());
        project.setMissions(projetFound.getMissions());
        project.setFactures(projetFound.getFactures());
        project.setDocuments(projetFound.getDocuments());
        project.setCoutReel(projetFound.getCoutReel());
        project.setProgression(projetFound.getProgression());

        project.setNom(command.getNom());
        project.setDescription(command.getDescription());
        project.setBudget(command.getBudget());
        project.setType(command.getType());
        project.setPriorite(command.getPriorite());
        project.setChefProjet(command.getChefProjet());
        project.setDateDebut(command.getDateDebut());
        project.setDateFin(command.getDateFin());
        project.setStatut(command.getStatut());

        project.setClient(foundClient);
        project.setId(command.getId());
        projectRepositoryService.update(project);
        return null; // retour Void
    }
}
