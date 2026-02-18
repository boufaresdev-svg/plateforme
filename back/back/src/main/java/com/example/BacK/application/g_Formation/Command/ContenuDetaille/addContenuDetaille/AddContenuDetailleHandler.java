package com.example.BacK.application.g_Formation.Command.ContenuDetaille.addContenuDetaille;

import com.example.BacK.application.g_Formation.Command.ContenuDetaille.ContentLevelDto;
import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Formation.ContenuDetaille;
import com.example.BacK.domain.g_Formation.ContentLevel;
import com.example.BacK.domain.g_Formation.JourFormation;
import com.example.BacK.infrastructure.services.g_Formation.ContenuDetailleRepositoryService;
import com.example.BacK.infrastructure.services.g_Formation.JourFormationRepositoryService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component("AddContenuDetailleHandler")
public class AddContenuDetailleHandler implements RequestHandler<AddContenuDetailleCommand, AddContenuDetailleResponse> {

    private final ContenuDetailleRepositoryService contenuDetailleRepositoryService;
    private final JourFormationRepositoryService jourFormationRepositoryService;

    public AddContenuDetailleHandler(
            ContenuDetailleRepositoryService contenuDetailleRepositoryService,
            JourFormationRepositoryService jourFormationRepositoryService) {
        this.contenuDetailleRepositoryService = contenuDetailleRepositoryService;
        this.jourFormationRepositoryService = jourFormationRepositoryService;
    }

    @Override
    public AddContenuDetailleResponse handle(AddContenuDetailleCommand command) {
        // Validate jour formation exists only if provided
        JourFormation jourFormation = null;
        if (command.getIdJourFormation() != null) {
            jourFormation = jourFormationRepositoryService
                    .getJourFormationById(command.getIdJourFormation())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "JourFormation non trouvé avec l'ID : " + command.getIdJourFormation()
                    ));
        }

        // Create new contenu detaille
        ContenuDetaille contenuDetaille = new ContenuDetaille();
        contenuDetaille.setTitre(command.getTitre());
        contenuDetaille.setContenusCles(command.getContenusCles());
        contenuDetaille.setMethodesPedagogiques(command.getMethodesPedagogiques());
        contenuDetaille.setDureeTheorique(command.getDureeTheorique());
        contenuDetaille.setDureePratique(command.getDureePratique());
        contenuDetaille.setTags(command.getTags());
        
        // Convert DTOs to entities with bidirectional relationship
        if (command.getLevels() != null) {
            Set<ContentLevel> levels = new HashSet<>();
            for (ContentLevelDto dto : command.getLevels()) {
                ContentLevel level = new ContentLevel();
                level.setLevelNumber(dto.getLevelNumber());
                level.setTheorieContent(dto.getTheorieContent());
                level.setPratiqueContent(dto.getPratiqueContent());
                level.setDureeTheorique(dto.getDureeTheorique());
                level.setDureePratique(dto.getDureePratique());
                level.setFormationLevels(dto.getFormationLevels());
                level.setContenuDetaille(contenuDetaille);
                levels.add(level);
            }
            contenuDetaille.setLevels(levels);
        }
        contenuDetaille.setJourFormation(jourFormation); // Can be null

        // Save and return response
        ContenuDetaille saved = contenuDetailleRepositoryService.saveContenuDetaille(contenuDetaille);

        AddContenuDetailleResponse response = new AddContenuDetailleResponse();
        response.setIdContenuDetaille(saved.getIdContenuDetaille());
        response.setTitre(saved.getTitre());
        response.setContenusCles(saved.getContenusCles());
        response.setMethodesPedagogiques(saved.getMethodesPedagogiques());
        response.setDureeTheorique(saved.getDureeTheorique());
        response.setDureePratique(saved.getDureePratique());
        response.setLevels(saved.getLevels());
        response.setMessage("ContenuDetaille ajouté avec succès !");

        return response;
    }
}
