package com.example.BacK.application.g_Formation.Command.ObjectifGlobal.deleteObjectifGlobal;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Formation.Formation;
import com.example.BacK.domain.g_Formation.ObjectifGlobal;
import com.example.BacK.infrastructure.repository.g_Formation.FormationRepository;
import com.example.BacK.infrastructure.repository.g_Formation.ObjectifGlobalRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component("DeleteObjectifGlobalHandler")
public class DeleteObjectifGlobalHandler implements RequestHandler<DeleteObjectifGlobalCommand, Void> {

    private final ObjectifGlobalRepository objectifGlobalRepository;
    private final FormationRepository formationRepository;
    private final EntityManager entityManager;

    public DeleteObjectifGlobalHandler(
            ObjectifGlobalRepository objectifGlobalRepository,
            FormationRepository formationRepository,
            EntityManager entityManager) {
        this.objectifGlobalRepository = objectifGlobalRepository;
        this.formationRepository = formationRepository;
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public Void handle(DeleteObjectifGlobalCommand command) {
        if (command.getIdobjectifglobal() == null) {
            throw new IllegalArgumentException("L'ID de l'objectif global ne peut pas être nul.");
        }

        if (!objectifGlobalRepository.existsById(command.getIdobjectifglobal())) {
            throw new IllegalArgumentException(
                    "Objectif global non trouvé avec l'ID : " + command.getIdobjectifglobal()
            );
        }

        // Get the ObjectifGlobal to delete
        ObjectifGlobal objectifGlobal = objectifGlobalRepository
                .findById(command.getIdobjectifglobal())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Objectif global non trouvé avec l'ID : " + command.getIdobjectifglobal()
                ));

        // Find all formations that reference this ObjectifGlobal and remove the association
        List<Formation> allFormations = formationRepository.findAll();
        for (Formation formation : allFormations) {
            if (formation.getObjectifsGlobaux() != null && 
                formation.getObjectifsGlobaux().contains(objectifGlobal)) {
                formation.getObjectifsGlobaux().remove(objectifGlobal);
                formationRepository.save(formation);
            }
        }

        // Flush all changes to ensure join table entries are deleted before deleting the entity
        entityManager.flush();
        entityManager.clear();

        // Now safe to delete the ObjectifGlobal
        objectifGlobalRepository.deleteById(command.getIdobjectifglobal());
        
        return null;
    }
}
