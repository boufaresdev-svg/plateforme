package com.example.BacK.application.g_Formation.Command.Formation.duplicateFormation;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Formation.*;
import com.example.BacK.infrastructure.repository.g_Formation.FormationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component("DuplicateFormationHandler")
public class DuplicateFormationHandler implements RequestHandler<DuplicateFormationCommand, DuplicateFormationResponse> {

    private final FormationRepository formationRepository;

    public DuplicateFormationHandler(FormationRepository formationRepository) {
        this.formationRepository = formationRepository;
    }

    @Override
    @Transactional
    public DuplicateFormationResponse handle(DuplicateFormationCommand command) {
        try {
            // 1. Load the original formation with all relationships
            Formation original = formationRepository.findById(command.getIdFormation())
                    .orElseThrow(() -> new IllegalArgumentException("Formation not found with ID: " + command.getIdFormation()));

            // 2. Create new formation with basic properties
            Formation duplicate = new Formation();
            duplicate.setTheme(original.getTheme() + " (Copie)");
            duplicate.setDescriptionTheme(original.getDescriptionTheme());
            duplicate.setObjectifSpecifiqueGlobal(original.getObjectifSpecifiqueGlobal());
            duplicate.setTags(original.getTags());
            duplicate.setNombreHeures(original.getNombreHeures());
            duplicate.setPrix(original.getPrix());
            duplicate.setNombreMax(original.getNombreMax());
            duplicate.setPopulationCible(original.getPopulationCible());
            duplicate.setTypeFormation(original.getTypeFormation());
            duplicate.setNiveau(original.getNiveau());
            duplicate.setStatut(original.getStatut());

            // 3. Copy relationships (reference only, no deep copy needed)
            duplicate.setDomaine(original.getDomaine());
            duplicate.setType(original.getType());
            duplicate.setCategorie(original.getCategorie());
            duplicate.setSousCategorie(original.getSousCategorie());
            duplicate.setFormateurs(new ArrayList<>(original.getFormateurs()));

        // 4. Deep copy ObjectifGlobal (create new instances)
        List<ObjectifGlobal> newObjectifsGlobaux = new ArrayList<>();
        Map<Long, ObjectifGlobal> globalMapping = new HashMap<>();
        
        if (original.getObjectifsGlobaux() != null) {
            for (ObjectifGlobal originalGlobal : original.getObjectifsGlobaux()) {
                ObjectifGlobal newGlobal = new ObjectifGlobal();
                newGlobal.setLibelle(originalGlobal.getLibelle());
                newGlobal.setDescription(originalGlobal.getDescription());
                newGlobal.setTags(originalGlobal.getTags());
                newObjectifsGlobaux.add(newGlobal);
                globalMapping.put(originalGlobal.getIdObjectifGlobal(), newGlobal);
            }
        }
        duplicate.setObjectifsGlobaux(newObjectifsGlobaux);

        // 5. Deep copy ObjectifSpecifique with their relationships
        List<ObjectifSpecifique> newObjectifsSpecifiques = new ArrayList<>();
        Map<Long, ObjectifSpecifique> specificMapping = new HashMap<>();

        if (original.getObjectifsSpecifiques() != null) {
            for (ObjectifSpecifique originalSpecific : original.getObjectifsSpecifiques()) {
                ObjectifSpecifique newSpecific = new ObjectifSpecifique();
                newSpecific.setTitre(originalSpecific.getTitre());
                newSpecific.setDescription(originalSpecific.getDescription());
                newSpecific.setTags(originalSpecific.getTags());
                
                // Link to duplicated ObjectifGlobal if exists
                if (originalSpecific.getObjectifGlobal() != null) {
                    ObjectifGlobal newGlobal = globalMapping.get(originalSpecific.getObjectifGlobal().getIdObjectifGlobal());
                    newSpecific.setObjectifGlobal(newGlobal);
                }
                
                newObjectifsSpecifiques.add(newSpecific);
                specificMapping.put(originalSpecific.getIdObjectifSpec(), newSpecific);
            }
        }
        duplicate.setObjectifsSpecifiques(newObjectifsSpecifiques);

        // 6. Deep copy ProgrammeDetaile with nested structures
        List<ProgrammeDetaile> newProgrammes = new ArrayList<>();
        
        if (original.getProgrammesDetailes() != null) {
            for (ProgrammeDetaile originalProgramme : original.getProgrammesDetailes()) {
                ProgrammeDetaile newProgramme = new ProgrammeDetaile();
                newProgramme.setTitre(originalProgramme.getTitre());
                newProgramme.setFormation(duplicate);
                
                // Deep copy JourFormation
                List<JourFormation> newJours = new ArrayList<>();
                if (originalProgramme.getJoursFormation() != null) {
                    for (JourFormation originalJour : originalProgramme.getJoursFormation()) {
                        JourFormation newJour = new JourFormation();
                        newJour.setNumeroJour(originalJour.getNumeroJour());
                        newJour.setProgrammeDetaile(newProgramme);
                        
                        // Deep copy ContenuDetaille
                        List<ContenuDetaille> newContenusDetailles = new ArrayList<>();
                        if (originalJour.getContenusDetailles() != null) {
                            for (ContenuDetaille originalContenu : originalJour.getContenusDetailles()) {
                                ContenuDetaille newContenu = duplicateContenuDetaille(originalContenu);
                                newContenu.setJourFormation(newJour);
                                newContenusDetailles.add(newContenu);
                            }
                        }
                        newJour.setContenusDetailles(newContenusDetailles);
                        newJours.add(newJour);
                    }
                }
                newProgramme.setJoursFormation(newJours);
                newProgrammes.add(newProgramme);
            }
        }
        duplicate.setProgrammesDetailes(newProgrammes);

        // 7. Note: PlanFormation is NOT duplicated as it's session-specific
        // Users need to create new training plans for the duplicated formation

        // 8. Save the duplicated formation
        Formation saved = formationRepository.save(duplicate);

        return new DuplicateFormationResponse(
                saved.getIdFormation(),
                saved.getTheme(),
                "Formation dupliquée avec succès avec tous ses contenus"
        );
        
        } catch (Exception e) {
            log.error("❌ Error during duplication: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to duplicate formation: " + e.getMessage(), e);
        }
    }

    /**
     * Deep copy a ContenuDetaille with all its levels and files
     */
    private ContenuDetaille duplicateContenuDetaille(ContenuDetaille original) {
        ContenuDetaille newContenu = new ContenuDetaille();
        newContenu.setTitre(original.getTitre());
        newContenu.setContenusCles(original.getContenusCles() != null ? new ArrayList<>(original.getContenusCles()) : new ArrayList<>());
        newContenu.setMethodesPedagogiques(original.getMethodesPedagogiques());
        newContenu.setDureeTheorique(original.getDureeTheorique());
        newContenu.setDureePratique(original.getDureePratique());
        newContenu.setTags(original.getTags());
        
        // Deep copy ContentLevel
        Set<ContentLevel> newLevels = new HashSet<>();
        if (original.getLevels() != null) {
            for (ContentLevel originalLevel : original.getLevels()) {
                ContentLevel newLevel = new ContentLevel();
                newLevel.setLevelNumber(originalLevel.getLevelNumber());
                newLevel.setTheorieContent(originalLevel.getTheorieContent());
                newLevel.setPratiqueContent(originalLevel.getPratiqueContent());
                newLevel.setDureeTheorique(originalLevel.getDureeTheorique());
                newLevel.setDureePratique(originalLevel.getDureePratique());
                newLevel.setFormationLevels(originalLevel.getFormationLevels() != null ? new ArrayList<>(originalLevel.getFormationLevels()) : new ArrayList<>());
                
                // Deep copy LevelFile
                List<LevelFile> newFiles = new ArrayList<>();
                if (originalLevel.getFiles() != null) {
                    for (LevelFile originalFile : originalLevel.getFiles()) {
                        LevelFile newFile = new LevelFile();
                        newFile.setFileName(originalFile.getFileName());
                        newFile.setFilePath(originalFile.getFilePath());
                        newFile.setFileType(originalFile.getFileType());
                        newFile.setFileSize(originalFile.getFileSize());
                        newFile.setUploadDate(originalFile.getUploadDate());
                        newFile.setDescription(originalFile.getDescription());
                        newFiles.add(newFile);
                    }
                }
                newLevel.setFiles(newFiles);
                newLevel.setContenuDetaille(newContenu);
                newLevels.add(newLevel);
            }
        }
        newContenu.setLevels(newLevels);
        
        return newContenu;
    }
}
