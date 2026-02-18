package com.example.BacK.application.g_Formation.Command.ObjectifSpecifique.addObjectifSpecifique;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Formation.ContenuGlobal;
import com.example.BacK.domain.g_Formation.ObjectifGlobal;
import com.example.BacK.domain.g_Formation.ObjectifSpecifique;
import com.example.BacK.infrastructure.repository.g_Formation.ObjectifGlobalRepository;
import com.example.BacK.infrastructure.services.g_Formation.ContenuGlobalRepositoryService;
import com.example.BacK.infrastructure.services.g_Formation.ObjectifSpecifiqueRepositoryService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component("AddObjectifSpecifiqueHandler")
public class AddObjectifSpecifiqueHandler implements RequestHandler<AddObjectifSpecifiqueCommand, AddObjectifSpecifiqueResponse> {

    private final ObjectifSpecifiqueRepositoryService objectifSpecifiqueRepositoryService;
    private final ContenuGlobalRepositoryService contenuGlobalRepositoryService;
    private final ObjectifGlobalRepository objectifGlobalRepository;
    private final ModelMapper modelMapper;

    public AddObjectifSpecifiqueHandler(ObjectifSpecifiqueRepositoryService objectifSpecifiqueRepositoryService,
                                        ContenuGlobalRepositoryService contenuGlobalRepositoryService,
                                        ObjectifGlobalRepository objectifGlobalRepository,
                                        ModelMapper modelMapper) {
        this.objectifSpecifiqueRepositoryService = objectifSpecifiqueRepositoryService;
        this.contenuGlobalRepositoryService = contenuGlobalRepositoryService;
        this.objectifGlobalRepository = objectifGlobalRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public AddObjectifSpecifiqueResponse handle(AddObjectifSpecifiqueCommand command) {

        ObjectifSpecifique objectifSpecifique = modelMapper.map(command, ObjectifSpecifique.class);

        if (command.getIdContenuGlobal() != null) {
            ContenuGlobal contenuGlobal = contenuGlobalRepositoryService.getContenuGlobalById(command.getIdContenuGlobal())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Contenu global non trouvé avec l'ID : " + command.getIdContenuGlobal()));
            objectifSpecifique.setContenuGlobal(contenuGlobal);
        }

        if (command.getIdObjectifGlobal() != null) {
            ObjectifGlobal objectifGlobal = objectifGlobalRepository.findById(command.getIdObjectifGlobal())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Objectif global non trouvé avec l'ID : " + command.getIdObjectifGlobal()));
            objectifSpecifique.setObjectifGlobal(objectifGlobal);
        }

        ObjectifSpecifique saved = objectifSpecifiqueRepositoryService.saveObjectifSpecifique(objectifSpecifique);

        return new AddObjectifSpecifiqueResponse(
                saved.getIdObjectifSpec(),
                "Objectif spécifique ajouté avec succès !"
        );
    }
}
