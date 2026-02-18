package com.example.BacK.application.g_Formation.Query.ContenuJourFormation.GetAssignedContenus;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Formation.ContenuDetaille;
import com.example.BacK.domain.g_Formation.ContentLevel;
import com.example.BacK.domain.g_Formation.ContenuJourFormation;
import com.example.BacK.infrastructure.services.g_Formation.ContenuJourFormationRepositoryService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component("GetAssignedContenusHandler")
public class GetAssignedContenusHandler implements RequestHandler<GetAssignedContenusQuery, List<GetAssignedContenusResponse>> {

    private final ContenuJourFormationRepositoryService contenuJourFormationRepositoryService;

    public GetAssignedContenusHandler(ContenuJourFormationRepositoryService contenuJourFormationRepositoryService) {
        this.contenuJourFormationRepositoryService = contenuJourFormationRepositoryService;
    }

    @Override
    public List<GetAssignedContenusResponse> handle(GetAssignedContenusQuery query) {
        ContenuJourFormation contenuJour = contenuJourFormationRepositoryService
                .getContenuJourFormationByIdWithContenusDetailles(query.getIdContenuJour())
                .orElseThrow(() -> new IllegalArgumentException(
                        "ContenuJourFormation non trouvé avec l'ID : " + query.getIdContenuJour()));

        if (contenuJour.getContenuAssignments() == null || contenuJour.getContenuAssignments().isEmpty()) {
            return new ArrayList<>();
        }

        // Map from assignments to include niveau information and get hours from ALL levels
        return contenuJour.getContenuAssignments().stream()
                .map(assignment -> {
                    ContenuDetaille contenu = assignment.getContenuDetaille();
                    
                    // Get hours by summing ALL levels (not just the assigned niveau)
                    Double dureeTheorique = 0.0;
                    Double dureePratique = 0.0;
                    
                    if (contenu.getLevels() != null && !contenu.getLevels().isEmpty()) {
                        // Sum hours from ALL levels
                        for (ContentLevel level : contenu.getLevels()) {
                            dureeTheorique += level.getDureeTheorique() != null ? level.getDureeTheorique() : 0.0;
                            dureePratique += level.getDureePratique() != null ? level.getDureePratique() : 0.0;
                        }
                    } else {
                        // Fallback to contenu detaille direct hours
                        dureeTheorique = contenu.getDureeTheorique() != null ? contenu.getDureeTheorique() : 0.0;
                        dureePratique = contenu.getDureePratique() != null ? contenu.getDureePratique() : 0.0;
                    }
                    
                    return new GetAssignedContenusResponse(
                            contenu.getIdContenuDetaille(),
                            contenu.getTitre(),
                            contenu.getContenusCles(),
                            contenu.getMethodesPedagogiques(),
                            dureeTheorique,
                            dureePratique,
                            assignment.getNiveau(),
                            assignment.getNiveauLabel()
                    );
                })
                .collect(Collectors.toList());
    }
}
