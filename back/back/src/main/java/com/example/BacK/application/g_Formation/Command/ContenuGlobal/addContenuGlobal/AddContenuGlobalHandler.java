package com.example.BacK.application.g_Formation.Command.ContenuGlobal.addContenuGlobal;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Formation.ContenuGlobal;
import com.example.BacK.domain.g_Formation.Formation;
import com.example.BacK.infrastructure.services.g_Formation.ContenuGlobalRepositoryService;
import com.example.BacK.infrastructure.services.g_Formation.FormationRepositoryService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component("AddContenuGlobalHandler")
public class AddContenuGlobalHandler implements RequestHandler<AddContenuGlobalCommand, AddContenuGlobalResponse> {

    private final ContenuGlobalRepositoryService contenuGlobalRepositoryService;
    private final FormationRepositoryService formationRepositoryService;
    private final ModelMapper modelMapper;

    public AddContenuGlobalHandler(ContenuGlobalRepositoryService contenuGlobalRepositoryService,
                                   FormationRepositoryService formationRepositoryService,
                                   ModelMapper modelMapper) {
        this.contenuGlobalRepositoryService = contenuGlobalRepositoryService;
        this.formationRepositoryService = formationRepositoryService;
        this.modelMapper = modelMapper;
    }

    @Override
    public AddContenuGlobalResponse handle(AddContenuGlobalCommand command) {
        ContenuGlobal contenuGlobal = modelMapper.map(command, ContenuGlobal.class);

        if (command.getIdFormation() != null) {
            Formation formation = formationRepositoryService
                    .getFormationById(command.getIdFormation())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Formation non trouvée avec l’ID : " + command.getIdFormation()
                    ));
            contenuGlobal.setFormation(formation);
        }

        ContenuGlobal saved = contenuGlobalRepositoryService.saveContenuGlobal(contenuGlobal);

        return new AddContenuGlobalResponse(
                saved.getIdContenuGlobal(),
                "Contenu global ajouté avec succès !"
        );
    }
}
