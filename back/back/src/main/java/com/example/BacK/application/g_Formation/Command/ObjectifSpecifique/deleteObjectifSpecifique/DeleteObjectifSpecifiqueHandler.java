package com.example.BacK.application.g_Formation.Command.ObjectifSpecifique.deleteObjectifSpecifique;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Formation.Formation;
import com.example.BacK.domain.g_Formation.ObjectifSpecifique;
import com.example.BacK.infrastructure.repository.g_Formation.FormationRepository;
import com.example.BacK.infrastructure.services.g_Formation.ObjectifSpecifiqueRepositoryService;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component("DeleteObjectifSpecifiqueHandler")
public class DeleteObjectifSpecifiqueHandler implements RequestHandler<DeleteObjectifSpecifiqueCommand, Void> {

    private final ObjectifSpecifiqueRepositoryService objectifSpecifiqueRepositoryService;
    private final FormationRepository formationRepository;
    private final EntityManager entityManager;

    public DeleteObjectifSpecifiqueHandler(
            ObjectifSpecifiqueRepositoryService objectifSpecifiqueRepositoryService,
            FormationRepository formationRepository,
            EntityManager entityManager) {
        this.objectifSpecifiqueRepositoryService = objectifSpecifiqueRepositoryService;
        this.formationRepository = formationRepository;
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public Void handle(DeleteObjectifSpecifiqueCommand command) {
        if (command.getIdobjectifspecifique() == null) {
            throw new IllegalArgumentException("L'ID de l'objectif spécifique ne peut pas être nul.");
        }

        if (!objectifSpecifiqueRepositoryService.existsById(command.getIdobjectifspecifique())) {
            throw new IllegalArgumentException(
                    "Objectif spécifique non trouvé avec l'ID : " + command.getIdobjectifspecifique()
            );
        }

        // Get the ObjectifSpecifique to delete
        ObjectifSpecifique objectifSpecifique = objectifSpecifiqueRepositoryService
                .getObjectifSpecifiqueById(command.getIdobjectifspecifique())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Objectif spécifique non trouvé avec l'ID : " + command.getIdobjectifspecifique()
                ));

        // Find all formations that reference this ObjectifSpecifique and remove the association
        List<Formation> allFormations = formationRepository.findAll();
        for (Formation formation : allFormations) {
            if (formation.getObjectifsSpecifiques() != null && 
                formation.getObjectifsSpecifiques().contains(objectifSpecifique)) {
                formation.getObjectifsSpecifiques().remove(objectifSpecifique);
                formationRepository.save(formation);
            }
        }

        // Flush all changes to ensure join table entries are deleted before deleting the entity
        entityManager.flush();
        entityManager.clear();

        // Now safe to delete the ObjectifSpecifique
        objectifSpecifiqueRepositoryService.deleteObjectifSpecifique(command.getIdobjectifspecifique());
        
        return null;
    }
}
