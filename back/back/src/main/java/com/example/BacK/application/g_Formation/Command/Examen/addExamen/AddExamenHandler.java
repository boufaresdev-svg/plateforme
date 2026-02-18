package com.example.BacK.application.g_Formation.Command.Examen.addExamen;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Formation.Apprenant;
import com.example.BacK.domain.g_Formation.Examen;
import com.example.BacK.infrastructure.services.g_Formation.ApprenantRepositoryService;
import com.example.BacK.infrastructure.services.g_Formation.ExamenRepositoryService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component("AddExamenHandler")
public class AddExamenHandler implements RequestHandler<AddExamenCommand, AddExamenResponse> {

    private final ExamenRepositoryService examenRepositoryService;
    private final ApprenantRepositoryService apprenantRepositoryService;
    private final ModelMapper modelMapper;

    public AddExamenHandler(
            ExamenRepositoryService examenRepositoryService,
            ApprenantRepositoryService apprenantRepositoryService,
            ModelMapper modelMapper
    ) {
        this.examenRepositoryService = examenRepositoryService;
        this.apprenantRepositoryService = apprenantRepositoryService;
        this.modelMapper = modelMapper;

        this.modelMapper.typeMap(AddExamenCommand.class, Examen.class)
                .addMappings(mapper -> mapper.skip(Examen::setIdExamen));
    }

    @Override
    public AddExamenResponse handle(AddExamenCommand command) {

        Examen examen = modelMapper.map(command, Examen.class);

        examen.setIdExamen(null); // sécurité

        if (command.getIdApprenant() == null) {
            throw new IllegalArgumentException("idApprenant est obligatoire pour créer un examen.");
        }

        Apprenant apprenant = apprenantRepositoryService
                .getApprenantById(command.getIdApprenant())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Aucun apprenant trouvé avec l’ID : " + command.getIdApprenant()
                ));

        examen.setApprenant(apprenant);

        Examen saved = examenRepositoryService.saveExamen(examen);

        // Réponse
        return new AddExamenResponse(
                saved.getIdExamen(),
                "Examen ajouté avec succès !"
        );
    }
}
