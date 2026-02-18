package com.example.BacK.application.g_Formation.Command.Formation.updateFormation;
import com.example.BacK.application.g_Formation.Command.Formation.addFormation.AddFormationCommand;
import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Formation.*;
import com.example.BacK.infrastructure.services.g_Formation.*;
import com.example.BacK.infrastructure.repository.g_Formation.ObjectifGlobalRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component("UpdateFormationHandler")
public class UpdateFormationHandler implements RequestHandler<UpdateFormationCommand, Void> {

    private static final Logger logger = LoggerFactory.getLogger(UpdateFormationHandler.class);
    
    private final FormationRepositoryService formationRepositoryService;
    private final DomaineRepositoryService domaineRepositoryService;
    private final TypeRepositoryService typeRepositoryService;
    private final CategorieRepositoryService categorieRepositoryService;
    private final SousCategorieRepositoryService sousCategorieService;
    private final ObjectifGlobalRepository objectifGlobalRepository;
    private final ModelMapper modelMapper;

    public UpdateFormationHandler(
            FormationRepositoryService formationRepositoryService,
            DomaineRepositoryService domaineRepositoryService,
            TypeRepositoryService typeRepositoryService,
            CategorieRepositoryService categorieRepositoryService,
            SousCategorieRepositoryService sousCategorieService,
            ObjectifGlobalRepository objectifGlobalRepository,
            ModelMapper modelMapper
    ) {
        this.formationRepositoryService = formationRepositoryService;
        this.domaineRepositoryService = domaineRepositoryService;
        this.typeRepositoryService = typeRepositoryService;
        this.categorieRepositoryService = categorieRepositoryService;
        this.sousCategorieService = sousCategorieService;
        this.objectifGlobalRepository = objectifGlobalRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public Void handle(UpdateFormationCommand command) {

        Formation existingFormation = formationRepositoryService.getFormationById(command.getIdFormation())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Formation non trouvée avec l'ID : " + command.getIdFormation()
                ));

        existingFormation.setTheme(command.getTheme());
        existingFormation.setDescriptionTheme(command.getDescriptionTheme());
        
        // Fetch ObjectifGlobal entities from IDs
        if (command.getObjectifsGlobauxIds() != null && !command.getObjectifsGlobauxIds().isEmpty()) {
            List<ObjectifGlobal> objectifsGlobaux = new ArrayList<>();
            for (Long id : command.getObjectifsGlobauxIds()) {
                objectifGlobalRepository.findById(id).ifPresent(objectifsGlobaux::add);
            }
            existingFormation.setObjectifsGlobaux(objectifsGlobaux);
        }
        
        existingFormation.setObjectifSpecifiqueGlobal(command.getObjectifSpecifiqueGlobal());
        existingFormation.setTags(command.getTags());
        existingFormation.setNombreHeures(command.getNombreHeures());
        existingFormation.setPrix(command.getPrix());
        existingFormation.setNombreMax(command.getNombreMax());
        existingFormation.setPopulationCible(command.getPopulationCible());
        existingFormation.setImageUrl(command.getImageUrl());
        existingFormation.setTypeFormation(command.getTypeFormation());
        
        // ⚠️ TEMPORARILY SKIP niveau updates due to CHECK constraint issues
        // The database has a CHECK constraint that's rejecting our niveau values
        // TODO: Investigate constraint definition and fix converter to match expected format
        logger.info("⚠️ Skipping niveau update (CHECK constraint issue). Current value: {}", existingFormation.getNiveau());

        if (command.getIdDomaine() != null) {
            Domaine domaine = domaineRepositoryService.getDomaineById(command.getIdDomaine())
                    .orElseThrow(() -> new IllegalArgumentException("Domaine non trouvé avec ID : " + command.getIdDomaine()));
            existingFormation.setDomaine(domaine);
        }

        if (command.getIdType() != null) {
            Type type = typeRepositoryService.getTypeById(command.getIdType())
                    .orElseThrow(() -> new IllegalArgumentException("Type non trouvé avec ID : " + command.getIdType()));
            existingFormation.setType(type);
        }

        if (command.getIdCategorie() != null) {
            Categorie categorie = categorieRepositoryService.getCategorieById(command.getIdCategorie())
                    .orElseThrow(() -> new IllegalArgumentException("Catégorie non trouvée avec ID : " + command.getIdCategorie()));
            existingFormation.setCategorie(categorie);
        }

        if (command.getIdSousCategorie() != null) {
            SousCategorie sousCategorie = sousCategorieService.getSousCategorieById(command.getIdSousCategorie())
                    .orElseThrow(() -> new IllegalArgumentException("Sous-catégorie non trouvée avec ID : " + command.getIdSousCategorie()));
            existingFormation.setSousCategorie(sousCategorie);
        }

        // Update Programme Détaillé
        // First, remove existing programmes
        existingFormation.getProgrammesDetailes().clear();
        
        // Then add new ones from command
        if (command.getProgrammesDetailes() != null && !command.getProgrammesDetailes().isEmpty()) {
            for (AddFormationCommand.ProgrammeDetaileDTO programmeDTO : command.getProgrammesDetailes()) {
                ProgrammeDetaile programme = new ProgrammeDetaile();
                programme.setTitre(programmeDTO.getTitre());
                programme.setFormation(existingFormation);
                
                if (programmeDTO.getJours() != null) {
                    for (AddFormationCommand.JourFormationDTO jourDTO : programmeDTO.getJours()) {
                        JourFormation jour = new JourFormation();
                        jour.setNumeroJour(jourDTO.getNumeroJour());
                        jour.setProgrammeDetaile(programme);
                        
                        if (jourDTO.getContenus() != null) {
                            for (AddFormationCommand.ContenuDetailleDTO contenuDTO : jourDTO.getContenus()) {
                                ContenuDetaille contenu = new ContenuDetaille();
                                contenu.setContenusCles(contenuDTO.getContenusCles());
                                contenu.setMethodesPedagogiques(contenuDTO.getMethodesPedagogiques());
                                contenu.setDureeTheorique(contenuDTO.getDureeTheorique());
                                contenu.setDureePratique(contenuDTO.getDureePratique());
                                contenu.setJourFormation(jour);
                                
                                jour.getContenusDetailles().add(contenu);
                            }
                        }
                        
                        programme.getJoursFormation().add(jour);
                    }
                }
                
                existingFormation.getProgrammesDetailes().add(programme);
            }
        }

        formationRepositoryService.updateFormation(command.getIdFormation(), existingFormation);

        return null;
    }
}
