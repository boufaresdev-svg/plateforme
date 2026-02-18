package com.example.BacK.application.g_Formation.Command.PlanFormation.addPlanFormation;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Formation.Formation;
import com.example.BacK.domain.g_Formation.Formateur;
import com.example.BacK.domain.g_Formation.PlanFormation;
import com.example.BacK.infrastructure.services.g_Formation.FormationRepositoryService;
import com.example.BacK.infrastructure.services.g_Formation.FormateurRepositoryService;
import com.example.BacK.infrastructure.services.g_Formation.PlanFormationRepositoryService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component("AddPlanFormationHandler")
public class AddPlanFormationHandler implements RequestHandler<AddPlanFormationCommand, AddPlanFormationResponse> {

    private final PlanFormationRepositoryService planFormationRepositoryService;
    private final FormationRepositoryService formationRepositoryService;
    private final FormateurRepositoryService formateurRepositoryService;
    private final ModelMapper modelMapper;

    public AddPlanFormationHandler(
            PlanFormationRepositoryService planFormationRepositoryService,
            FormationRepositoryService formationRepositoryService,
            FormateurRepositoryService formateurRepositoryService,
            ModelMapper modelMapper) {
        this.planFormationRepositoryService = planFormationRepositoryService;
        this.formationRepositoryService = formationRepositoryService;
        this.formateurRepositoryService = formateurRepositoryService;
        this.modelMapper = modelMapper;
    }

    @Override
    public AddPlanFormationResponse handle(AddPlanFormationCommand command) {

        PlanFormation planFormation = new PlanFormation();
        planFormation.setTitre(command.getTitre());
        planFormation.setDescription(command.getDescription());
        planFormation.setDateLancement(command.getDateLancement());
        planFormation.setDateDebut(command.getDateDebut());
        planFormation.setDateFin(command.getDateFin());
        planFormation.setDateFinReel(command.getDateFinReel());
        planFormation.setStatusFormation(command.getStatusFormation());
        planFormation.setNombreJours(command.getNombreJours());

        if (command.getIdFormation() != null) {
            Formation formation = formationRepositoryService.getFormationById(command.getIdFormation())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Formation non trouvée avec l'ID : " + command.getIdFormation()));
            planFormation.setFormation(formation);
        }

        if (command.getIdFormateur() != null) {
            Formateur formateur = formateurRepositoryService.getFormateurById(command.getIdFormateur())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Formateur non trouvé avec l'ID : " + command.getIdFormateur()));
            planFormation.setFormateur(formateur);
        }

        PlanFormation saved = planFormationRepositoryService.savePlanFormation(planFormation);

        return new AddPlanFormationResponse(
                saved.getIdPlanFormation(),
                "Plan de formation ajouté avec succès !"
        );
    }
}
