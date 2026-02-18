package com.example.BacK.application.g_Formation.Command.Type.updateType;


import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Formation.Domaine;
import com.example.BacK.domain.g_Formation.Type;
import com.example.BacK.infrastructure.services.g_Formation.DomaineRepositoryService;
import com.example.BacK.infrastructure.services.g_Formation.TypeRepositoryService;
import org.springframework.stereotype.Component;

@Component("UpdateTypeHandler")
public class UpdateTypeHandler implements RequestHandler<UpdateTypeCommand, Void> {

    private final TypeRepositoryService typeRepositoryService;
    private final DomaineRepositoryService domaineRepositoryService;

    public UpdateTypeHandler(TypeRepositoryService typeRepositoryService, DomaineRepositoryService domaineRepositoryService) {
        this.typeRepositoryService = typeRepositoryService;
        this.domaineRepositoryService = domaineRepositoryService;
    }

    @Override
    public Void handle(UpdateTypeCommand command) {
        Type type = typeRepositoryService.getTypeById(command.getIdType())
                .orElseThrow(() -> new IllegalArgumentException("Type non trouvé avec l'ID : " + command.getIdType()));

        type.setNom(command.getNom());
        type.setDescription(command.getDescription());
        
        // Update domaine relationship if domaineId is provided
        if (command.getDomaineId() != null) {
            Domaine domaine = domaineRepositoryService.getDomaineById(command.getDomaineId())
                    .orElseThrow(() -> new IllegalArgumentException("Domaine non trouvé avec l'ID : " + command.getDomaineId()));
            type.setDomaine(domaine);
        }

        typeRepositoryService.saveType(type);

        return null;
    }}
