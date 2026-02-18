package com.example.BacK.application.g_Formation.Command.ContenuJourFormation.copyAndLink;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Formation.ContenuJourFormation;
import com.example.BacK.domain.g_Formation.ContenuJourNiveauAssignment;
import com.example.BacK.domain.g_Formation.Formation;
import com.example.BacK.domain.g_Formation.ObjectifSpecifique;
import com.example.BacK.domain.g_Formation.PlanFormation;
import com.example.BacK.infrastructure.repository.g_Formation.ContenuJourNiveauAssignmentRepository;
import com.example.BacK.infrastructure.repository.g_Formation.FormationRepository;
import com.example.BacK.infrastructure.repository.g_Formation.PlanFormationRepository;
import com.example.BacK.infrastructure.services.g_Formation.ContenuJourFormationRepositoryService;
import com.example.BacK.infrastructure.services.g_Formation.ObjectifSpecifiqueRepositoryService;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;

@Component("CopyAndLinkContenuJourHandler")
public class CopyAndLinkContenuJourHandler implements RequestHandler<CopyAndLinkContenuJourCommand, Void> {

    private final ContenuJourFormationRepositoryService contenuJourFormationRepositoryService;
    private final ObjectifSpecifiqueRepositoryService objectifSpecifiqueRepositoryService;
    private final PlanFormationRepository planFormationRepository;
    private final FormationRepository formationRepository;
    private final ContenuJourNiveauAssignmentRepository contenuJourNiveauAssignmentRepository;

    public CopyAndLinkContenuJourHandler(ContenuJourFormationRepositoryService contenuJourFormationRepositoryService,
                                         ObjectifSpecifiqueRepositoryService objectifSpecifiqueRepositoryService,
                                         PlanFormationRepository planFormationRepository,
                                         FormationRepository formationRepository,
                                         ContenuJourNiveauAssignmentRepository contenuJourNiveauAssignmentRepository) {
        this.contenuJourFormationRepositoryService = contenuJourFormationRepositoryService;
        this.objectifSpecifiqueRepositoryService = objectifSpecifiqueRepositoryService;
        this.planFormationRepository = planFormationRepository;
        this.formationRepository = formationRepository;
        this.contenuJourNiveauAssignmentRepository = contenuJourNiveauAssignmentRepository;
    }

    @Override
    public Void handle(CopyAndLinkContenuJourCommand command) {
        // Fetch the formation
        Formation formation = formationRepository.findById(command.getFormationId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Formation non trouvée avec l'ID : " + command.getFormationId()));
        
        // Fetch the existing contenu jour to copy
        ContenuJourFormation sourceContenu = contenuJourFormationRepositoryService
                .getContenuJourFormationById(command.getContenuJourId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Contenu jour non trouvé avec l'ID : " + command.getContenuJourId()));

        // Fetch the objectif specifique to link to
        ObjectifSpecifique targetObjectif = objectifSpecifiqueRepositoryService
                .getObjectifSpecifiqueById(command.getObjectifSpecifiqueId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Objectif spécifique non trouvé avec l'ID : " + command.getObjectifSpecifiqueId()));

        // Fetch the plan formation to link to (optional)
        PlanFormation planFormation = null;
        if (command.getPlanFormationId() != null) {
            planFormation = planFormationRepository.findById(command.getPlanFormationId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Plan formation non trouvé avec l'ID : " + command.getPlanFormationId()));
        }

        // Create a copy of the source contenu jour
        ContenuJourFormation copiedContenu = new ContenuJourFormation();
        copiedContenu.setContenu(sourceContenu.getContenu());
        copiedContenu.setMoyenPedagogique(sourceContenu.getMoyenPedagogique());
        copiedContenu.setSupportPedagogique(sourceContenu.getSupportPedagogique());
        copiedContenu.setNbHeuresTheoriques(sourceContenu.getNbHeuresTheoriques());
        copiedContenu.setNbHeuresPratiques(sourceContenu.getNbHeuresPratiques());
        copiedContenu.setTags(sourceContenu.getTags());
        
        // Link the copied contenu to the target objectif specifique
        copiedContenu.setObjectifSpecifique(targetObjectif);
        
        // Link to the plan formation
        copiedContenu.setPlanFormation(planFormation);
        
        // Initialize the contenu assignments set
        copiedContenu.setContenuAssignments(new HashSet<>());

        // Save the copied contenu
        ContenuJourFormation saved = contenuJourFormationRepositoryService.saveContenuJourFormation(copiedContenu);
        
        // Copy contenu detaille assignments from source to target with optional level filtering
        if (command.getNiveau() != null) {
            // If a specific niveau is provided, only copy assignments for that niveau
            List<ContenuJourNiveauAssignment> sourceAssignments = 
                    contenuJourNiveauAssignmentRepository.findByContenuJour_IdContenuJourAndNiveau(
                            sourceContenu.getIdContenuJour(), command.getNiveau());
            
            for (ContenuJourNiveauAssignment assignment : sourceAssignments) {
                ContenuJourNiveauAssignment newAssignment = new ContenuJourNiveauAssignment(
                        saved,
                        assignment.getContenuDetaille(),
                        assignment.getNiveau(),
                        assignment.getNiveauLabel()
                );
                saved.getContenuAssignments().add(newAssignment);
                contenuJourNiveauAssignmentRepository.save(newAssignment);
            }
        } else {
            // Copy all assignments from source to target
            List<ContenuJourNiveauAssignment> sourceAssignments = 
                    contenuJourNiveauAssignmentRepository.findByContenuJour_IdContenuJour(
                            sourceContenu.getIdContenuJour());
            
            for (ContenuJourNiveauAssignment assignment : sourceAssignments) {
                ContenuJourNiveauAssignment newAssignment = new ContenuJourNiveauAssignment(
                        saved,
                        assignment.getContenuDetaille(),
                        assignment.getNiveau(),
                        assignment.getNiveauLabel()
                );
                saved.getContenuAssignments().add(newAssignment);
                contenuJourNiveauAssignmentRepository.save(newAssignment);
            }
        }
        
        // Add the copied contenu to the objectif specifique's list
        targetObjectif.getContenusJourFormation().add(saved);
        objectifSpecifiqueRepositoryService.saveObjectifSpecifique(targetObjectif);

        return null;
    }
}
