package com.example.BacK.application.g_Projet.Command.projet.add;


import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Client.Client;
import com.example.BacK.domain.g_Projet.Projet;
import com.example.BacK.infrastructure.services.g_Client.ClientRepositoryService;
import com.example.BacK.infrastructure.services.g_Projet.ProjectRepositoryService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component("AddProjetHandler")
public class AddProjetHandler implements RequestHandler<AddProjetCommand, AddProjetResponse> {

    private final ProjectRepositoryService projectRepositoryService;
    private final ClientRepositoryService clientRepositoryService;
    private final ModelMapper modelMapper;

    public AddProjetHandler(ProjectRepositoryService projectRepositoryService, ClientRepositoryService clientRepositoryService, ModelMapper modelMapper) {
        this.projectRepositoryService = projectRepositoryService;
        this.clientRepositoryService = clientRepositoryService;
        this.modelMapper = modelMapper;
    }

    @Override
    public AddProjetResponse handle(AddProjetCommand command) {
        Projet project = modelMapper.map(command, Projet.class);
        Client foundClient = clientRepositoryService.getByid(command.getClient());
        if (foundClient == null) {
            throw new RuntimeException("Client introuvable avec l'id : " + command.getClient());
        }
        project.setClient(foundClient);
        project.setCoutReel(Double.parseDouble("0"));
        project.setProgression(Double.parseDouble("0"));
        String id = projectRepositoryService.add(project);
        return new AddProjetResponse(id);
    }
}
