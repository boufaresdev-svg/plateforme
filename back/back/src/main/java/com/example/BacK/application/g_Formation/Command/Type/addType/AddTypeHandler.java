package com.example.BacK.application.g_Formation.Command.Type.addType;

import com.example.BacK.application.mediator.RequestHandler;
import org.springframework.stereotype.Component;
import com.example.BacK.domain.g_Formation.Domaine;
import com.example.BacK.domain.g_Formation.Type;
import com.example.BacK.infrastructure.services.g_Formation.DomaineRepositoryService;
import com.example.BacK.infrastructure.services.g_Formation.TypeRepositoryService;

@Component("AddTypeHandler")
public class AddTypeHandler implements RequestHandler<AddTypeCommand, AddTypeResponse> {

    private final TypeRepositoryService typeRepositoryService;
    private final DomaineRepositoryService domaineRepositoryService;

    public AddTypeHandler(TypeRepositoryService typeRepositoryService, DomaineRepositoryService domaineRepositoryService) {
        this.typeRepositoryService = typeRepositoryService;
        this.domaineRepositoryService = domaineRepositoryService;
    }

    @Override
    public AddTypeResponse handle(AddTypeCommand command) {
        Domaine domaine = domaineRepositoryService.getDomaineById(command.getDomaineId())
                .orElseThrow(() -> new IllegalArgumentException("Domaine non trouvé avec l'ID : " + command.getDomaineId()));

        Type type = new Type();
        type.setNom(command.getNom());
        type.setDescription(command.getDescription());
        type.setDomaine(domaine);

        Type savedType = typeRepositoryService.saveType(type);

        return new AddTypeResponse(savedType.getIdType(), "Type créé avec succès !");
    }
}

