package com.example.BacK.application.g_Formation.Command.PlanFormation.updatePlanFormation;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Formation.Formateur;
import com.example.BacK.domain.g_Formation.Formation;
import com.example.BacK.domain.g_Formation.PlanFormation;
import com.example.BacK.infrastructure.services.g_Formation.FormateurRepositoryService;
import com.example.BacK.infrastructure.services.g_Formation.FormationRepositoryService;
import com.example.BacK.infrastructure.services.g_Formation.PlanFormationRepositoryService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component("UpdatePlanFormationHandler")
public class UpdatePlanFormationHandler implements RequestHandler<UpdatePlanFormationCommand, Void> {

    private final PlanFormationRepositoryService planFormationRepositoryService;
    private final FormationRepositoryService formationRepositoryService;
    private final FormateurRepositoryService formateurRepositoryService;
    private final ModelMapper modelMapper;

    public UpdatePlanFormationHandler(PlanFormationRepositoryService planFormationRepositoryService,
                                      FormationRepositoryService formationRepositoryService,
                                      FormateurRepositoryService formateurRepositoryService,
                                      ModelMapper modelMapper) {
        this.planFormationRepositoryService = planFormationRepositoryService;
        this.formationRepositoryService = formationRepositoryService;
        this.formateurRepositoryService = formateurRepositoryService;
        this.modelMapper = modelMapper;
    }

    @Override
    public Void handle(UpdatePlanFormationCommand command) {

        if (command.getIdPlanFormation() == null) {
            throw new IllegalArgumentException("L'ID du plan de formation est requis pour la mise à jour.");
        }

        PlanFormation existing = planFormationRepositoryService.getPlanFormationById(command.getIdPlanFormation())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Aucun plan de formation trouvé avec l'ID : " + command.getIdPlanFormation()
                ));

        existing.setTitre(command.getTitre());
        existing.setDescription(command.getDescription());
        existing.setDateLancement(command.getDateLancement());
        existing.setDateDebut(command.getDateDebut());
        existing.setDateFin(command.getDateFin());
        existing.setDateFinReel(command.getDateFinReel());
        existing.setStatusFormation(command.getStatusFormation());
        existing.setNombreJours(command.getNombreJours());

        if (command.getIdFormation() != null) {
            Formation formation = formationRepositoryService.getFormationById(command.getIdFormation())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Formation non trouvée avec l'ID : " + command.getIdFormation()
                    ));
            existing.setFormation(formation);
        }

        if (command.getIdFormateur() != null) {
            Formateur formateur = formateurRepositoryService.getFormateurById(command.getIdFormateur())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Formateur non trouvé avec l'ID : " + command.getIdFormateur()
                    ));
            existing.setFormateur(formateur);
        }

        planFormationRepositoryService.updatePlanFormation(command.getIdPlanFormation(), existing);

        return null;
    }
}
