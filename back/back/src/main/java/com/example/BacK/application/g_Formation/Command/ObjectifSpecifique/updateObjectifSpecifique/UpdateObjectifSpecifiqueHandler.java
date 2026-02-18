package com.example.BacK.application.g_Formation.Command.ObjectifSpecifique.updateObjectifSpecifique;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Formation.ContenuGlobal;
import com.example.BacK.domain.g_Formation.ObjectifGlobal;
import com.example.BacK.domain.g_Formation.ObjectifSpecifique;
import com.example.BacK.infrastructure.repository.g_Formation.ObjectifGlobalRepository;
import com.example.BacK.infrastructure.services.g_Formation.ContenuGlobalRepositoryService;
import com.example.BacK.infrastructure.services.g_Formation.ObjectifSpecifiqueRepositoryService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component("UpdateObjectifSpecifiqueHandler")
public class UpdateObjectifSpecifiqueHandler implements RequestHandler<UpdateObjectifSpecifiqueCommand, Void> {

    private final ObjectifSpecifiqueRepositoryService objectifSpecifiqueRepositoryService;
    private final ContenuGlobalRepositoryService contenuGlobalRepositoryService;
    private final ObjectifGlobalRepository objectifGlobalRepository;
    private final ModelMapper modelMapper;

    public UpdateObjectifSpecifiqueHandler(ObjectifSpecifiqueRepositoryService objectifSpecifiqueRepositoryService,
                                           ContenuGlobalRepositoryService contenuGlobalRepositoryService,
                                           ObjectifGlobalRepository objectifGlobalRepository,
                                           ModelMapper modelMapper) {
        this.objectifSpecifiqueRepositoryService = objectifSpecifiqueRepositoryService;
        this.contenuGlobalRepositoryService = contenuGlobalRepositoryService;
        this.objectifGlobalRepository = objectifGlobalRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public Void handle(UpdateObjectifSpecifiqueCommand command) {
        if (command.getIdObjectifSpecifique() == null) {
            throw new IllegalArgumentException("L'ID de l’objectif spécifique ne peut pas être nul.");
        }

        ObjectifSpecifique objectif = objectifSpecifiqueRepositoryService
                .getObjectifSpecifiqueById(command.getIdObjectifSpecifique())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Objectif spécifique non trouvé avec l’ID : " + command.getIdObjectifSpecifique()
                ));

        objectif.setTitre(command.getTitre());
        objectif.setDescription(command.getDescription());

        if (command.getIdContenuGlobal() != null) {
            ContenuGlobal contenuGlobal = contenuGlobalRepositoryService
                    .getContenuGlobalById(command.getIdContenuGlobal())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Contenu global non trouvé avec l'ID : " + command.getIdContenuGlobal()
                    ));
            objectif.setContenuGlobal(contenuGlobal);
        } else {
            objectif.setContenuGlobal(null);
        }

        if (command.getIdObjectifGlobal() != null) {
            ObjectifGlobal objectifGlobal = objectifGlobalRepository
                    .findById(command.getIdObjectifGlobal())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Objectif global non trouvé avec l'ID : " + command.getIdObjectifGlobal()
                    ));
            objectif.setObjectifGlobal(objectifGlobal);
        } else {
            objectif.setObjectifGlobal(null);
        }

        objectifSpecifiqueRepositoryService.saveObjectifSpecifique(objectif);
        return null;
    }
}
