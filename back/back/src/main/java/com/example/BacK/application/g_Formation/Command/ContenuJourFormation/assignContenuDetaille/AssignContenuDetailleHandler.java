package com.example.BacK.application.g_Formation.Command.ContenuJourFormation.assignContenuDetaille;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Formation.ContenuDetaille;
import com.example.BacK.domain.g_Formation.ContenuJourFormation;
import com.example.BacK.domain.g_Formation.ContenuJourNiveauAssignment;
import com.example.BacK.infrastructure.repository.g_Formation.ContenuJourNiveauAssignmentRepository;
import com.example.BacK.infrastructure.services.g_Formation.ContenuDetailleRepositoryService;
import com.example.BacK.infrastructure.services.g_Formation.ContenuJourFormationRepositoryService;
import org.springframework.stereotype.Component;

import java.util.HashSet;

@Component("AssignContenuDetailleHandler")
public class AssignContenuDetailleHandler implements RequestHandler<AssignContenuDetailleCommand, AssignContenuDetailleResponse> {

    private final ContenuJourFormationRepositoryService contenuJourFormationRepositoryService;
    private final ContenuDetailleRepositoryService contenuDetailleRepositoryService;
    private final ContenuJourNiveauAssignmentRepository assignmentRepository;

    public AssignContenuDetailleHandler(
            ContenuJourFormationRepositoryService contenuJourFormationRepositoryService,
            ContenuDetailleRepositoryService contenuDetailleRepositoryService,
            ContenuJourNiveauAssignmentRepository assignmentRepository) {
        this.contenuJourFormationRepositoryService = contenuJourFormationRepositoryService;
        this.contenuDetailleRepositoryService = contenuDetailleRepositoryService;
        this.assignmentRepository = assignmentRepository;
    }

    @Override
    public AssignContenuDetailleResponse handle(AssignContenuDetailleCommand command) {
        if (command.getIdContenuJour() == null) {
            throw new IllegalArgumentException("idContenuJour cannot be null");
        }
        
        // Get the ContenuJourFormation
        ContenuJourFormation contenuJour = contenuJourFormationRepositoryService
                .getContenuJourFormationById(command.getIdContenuJour())
                .orElseThrow(() -> new IllegalArgumentException(
                    "ContenuJourFormation non trouvé avec l'ID : " + command.getIdContenuJour() + 
                    ". Veuillez vérifier que cet élément existe dans la base de données."));

        // Initialize assignments set if null
        if (contenuJour.getContenuAssignments() == null) {
            contenuJour.setContenuAssignments(new HashSet<>());
        }

        // If niveau is provided, this is an ADD operation for specific niveau
        // If niveau is null, this is a REPLACE operation (remove all and set new list)
        if (command.getNiveau() != null) {
            // ADD MODE: Add new assignments with niveau (don't clear existing)
            Integer niveau = command.getNiveau();
            String niveauLabel = command.getNiveauLabel() != null ? command.getNiveauLabel() : "DEBUTANT";
            
            if (command.getIdContenusDetailles() != null && !command.getIdContenusDetailles().isEmpty()) {
                for (Long idContenuDetaille : command.getIdContenusDetailles()) {
                    // Check if already assigned with same niveau
                    boolean alreadyAssigned = contenuJour.getContenuAssignments().stream()
                        .anyMatch(a -> a.getContenuDetaille().getIdContenuDetaille().equals(idContenuDetaille) 
                                    && a.getNiveau().equals(niveau));
                    
                    if (!alreadyAssigned) {
                        ContenuDetaille contenuDetaille = contenuDetailleRepositoryService
                                .getContenuDetailleById(idContenuDetaille)
                                .orElseThrow(() -> new IllegalArgumentException(
                                        "ContenuDetaille non trouvé avec l'ID : " + idContenuDetaille));
                        
                        // Create new assignment with niveau
                        ContenuJourNiveauAssignment assignment = new ContenuJourNiveauAssignment();
                        assignment.setContenuJour(contenuJour);
                        assignment.setContenuDetaille(contenuDetaille);
                        assignment.setNiveau(niveau);
                        assignment.setNiveauLabel(niveauLabel);
                        
                        contenuJour.getContenuAssignments().add(assignment);
                    }
                }
            }
        } else {
            // REPLACE MODE: Clear and set to match the provided list (for removal operations)
            contenuJour.getContenuAssignments().clear();
            
            // Re-add items from the list with default niveau (since we don't know the original niveaux)
            if (command.getIdContenusDetailles() != null && !command.getIdContenusDetailles().isEmpty()) {
                for (Long idContenuDetaille : command.getIdContenusDetailles()) {
                    ContenuDetaille contenuDetaille = contenuDetailleRepositoryService
                            .getContenuDetailleById(idContenuDetaille)
                            .orElseThrow(() -> new IllegalArgumentException(
                                    "ContenuDetaille non trouvé avec l'ID : " + idContenuDetaille));
                    
                    // Create assignment with default niveau
                    ContenuJourNiveauAssignment assignment = new ContenuJourNiveauAssignment();
                    assignment.setContenuJour(contenuJour);
                    assignment.setContenuDetaille(contenuDetaille);
                    assignment.setNiveau(1); // Default to Débutant
                    assignment.setNiveauLabel("DEBUTANT");
                    
                    contenuJour.getContenuAssignments().add(assignment);
                }
            }
        }

        // Save the updated ContenuJourFormation
        ContenuJourFormation saved = contenuJourFormationRepositoryService.saveContenuJourFormation(contenuJour);

        String message = command.getNiveau() != null 
            ? "Contenu ajouté avec succès au niveau " + command.getNiveauLabel()
            : "Contenus mis à jour avec succès";
            
        return new AssignContenuDetailleResponse(
                saved.getIdContenuJour(),
                message,
                saved.getContenuAssignments().size()
        );
    }
}
