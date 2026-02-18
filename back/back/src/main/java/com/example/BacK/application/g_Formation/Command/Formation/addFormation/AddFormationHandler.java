package com.example.BacK.application.g_Formation.Command.Formation.addFormation;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Formation.*;
import com.example.BacK.infrastructure.services.g_Formation.*;
import com.example.BacK.infrastructure.repository.g_Formation.ObjectifGlobalRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component("AddFormationHandler")
public class AddFormationHandler implements RequestHandler<AddFormationCommand, AddFormationResponse> {

    private final FormationRepositoryService formationRepositoryService;
    private final DomaineRepositoryService domaineRepositoryService;
    private final TypeRepositoryService typeRepositoryService;
    private final CategorieRepositoryService categorieRepositoryService;
    private final SousCategorieRepositoryService sousCategorieRepositoryService;
    private final ObjectifGlobalRepository objectifGlobalRepository;
    private final ModelMapper modelMapper;

    public AddFormationHandler(FormationRepositoryService formationRepositoryService,
                               DomaineRepositoryService domaineRepositoryService,
                               TypeRepositoryService typeRepositoryService,
                               CategorieRepositoryService categorieRepositoryService,
                               SousCategorieRepositoryService sousCategorieRepositoryService,
                               ObjectifGlobalRepository objectifGlobalRepository,
                               ModelMapper modelMapper) {
        this.formationRepositoryService = formationRepositoryService;
        this.domaineRepositoryService = domaineRepositoryService;
        this.typeRepositoryService = typeRepositoryService;
        this.categorieRepositoryService = categorieRepositoryService;
        this.sousCategorieRepositoryService = sousCategorieRepositoryService;
        this.objectifGlobalRepository = objectifGlobalRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public AddFormationResponse handle(AddFormationCommand command) {

        Formation formation = new Formation();
        formation.setTheme(command.getTheme());
        formation.setDescriptionTheme(command.getDescriptionTheme());
        
        // Fetch ObjectifGlobal entities from IDs
        if (command.getObjectifsGlobauxIds() != null && !command.getObjectifsGlobauxIds().isEmpty()) {
            List<ObjectifGlobal> objectifsGlobaux = new ArrayList<>();
            for (Long id : command.getObjectifsGlobauxIds()) {
                objectifGlobalRepository.findById(id).ifPresent(objectifsGlobaux::add);
            }
            formation.setObjectifsGlobaux(objectifsGlobaux);
        }
        
        formation.setObjectifSpecifiqueGlobal(command.getObjectifSpecifiqueGlobal());
        formation.setTags(command.getTags());
        formation.setNombreHeures(command.getNombreHeures());
        formation.setPrix(command.getPrix());
        formation.setNombreMax(command.getNombreMax());
        formation.setPopulationCible(command.getPopulationCible());
        formation.setImageUrl(command.getImageUrl());
        formation.setTypeFormation(command.getTypeFormation());
        // Don't set niveau for draft formations - it has a CHECK constraint that's problematic
        // The niveau will be set in a later step when the formation is published
        if (command.getStatut() != null && !command.getStatut().equals("Brouillon")) {
            formation.setNiveau(command.getNiveau());
        }
        formation.setStatut(command.getStatut() != null ? command.getStatut() : "Brouillon");

        if (command.getIdDomaine() != null) {
            Domaine domaine = domaineRepositoryService.getDomaineById(command.getIdDomaine())
                    .orElseThrow(() -> new IllegalArgumentException("Domaine non trouvé avec l'ID : " + command.getIdDomaine()));
            formation.setDomaine(domaine);
        }

        if (command.getIdType() != null) {
            Type type = typeRepositoryService.getTypeById(command.getIdType())
                    .orElseThrow(() -> new IllegalArgumentException("Type non trouvé avec l'ID : " + command.getIdType()));
            formation.setType(type);
        }

        if (command.getIdCategorie() != null) {
            Categorie categorie = categorieRepositoryService.getCategorieById(command.getIdCategorie())
                    .orElseThrow(() -> new IllegalArgumentException("Catégorie non trouvée avec l'ID : " + command.getIdCategorie()));
            formation.setCategorie(categorie);
        }

        if (command.getIdSousCategorie() != null) {
            SousCategorie sousCategorie = sousCategorieRepositoryService.getSousCategorieById(command.getIdSousCategorie())
                    .orElseThrow(() -> new IllegalArgumentException("Sous-catégorie non trouvée avec l'ID : " + command.getIdSousCategorie()));
            formation.setSousCategorie(sousCategorie);
        }

        // Process Programme Détaillé
        if (command.getProgrammesDetailes() != null && !command.getProgrammesDetailes().isEmpty()) {
            for (AddFormationCommand.ProgrammeDetaileDTO programmeDTO : command.getProgrammesDetailes()) {
                ProgrammeDetaile programme = new ProgrammeDetaile();
                programme.setTitre(programmeDTO.getTitre());
                programme.setFormation(formation);
                
                if (programmeDTO.getJours() != null) {
                    for (AddFormationCommand.JourFormationDTO jourDTO : programmeDTO.getJours()) {
                        JourFormation jour = new JourFormation();
                        jour.setNumeroJour(jourDTO.getNumeroJour());
                        jour.setProgrammeDetaile(programme);
                        
                        if (jourDTO.getContenus() != null) {
                            for (AddFormationCommand.ContenuDetailleDTO contenuDTO : jourDTO.getContenus()) {
                                ContenuDetaille contenu = new ContenuDetaille();
                                contenu.setTitre(contenuDTO.getTitre());
                                contenu.setContenusCles(contenuDTO.getContenusCles());
                                contenu.setMethodesPedagogiques(contenuDTO.getMethodesPedagogiques());
                                contenu.setDureeTheorique(contenuDTO.getDureeTheorique());
                                contenu.setDureePratique(contenuDTO.getDureePratique());
                                contenu.setJourFormation(jour);
                                
                                // Map levels if provided
                                if (contenuDTO.getLevels() != null && !contenuDTO.getLevels().isEmpty()) {
                                    for (AddFormationCommand.ContentLevelDTO levelDTO : contenuDTO.getLevels()) {
                                        ContentLevel level = new ContentLevel();
                                        level.setLevelNumber(levelDTO.getLevelNumber());
                                        level.setTheorieContent(levelDTO.getTheorieContent());
                                        level.setPratiqueContent(levelDTO.getPratiqueContent());
                                        level.setDureeTheorique(levelDTO.getDureeTheorique());
                                        level.setDureePratique(levelDTO.getDureePratique());
                                        
                                        contenu.getLevels().add(level);
                                    }
                                }
                                
                                jour.getContenusDetailles().add(contenu);
                            }
                        }
                        
                        programme.getJoursFormation().add(jour);
                    }
                }
                
                formation.getProgrammesDetailes().add(programme);
            }
        }

        Formation saved = formationRepositoryService.saveFormation(formation);

        return new AddFormationResponse(saved.getIdFormation(), "Formation ajoutée avec succès !");
    }
}


