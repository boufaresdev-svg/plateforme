package com.example.BacK.infrastructure.services.g_Formation;

import com.example.BacK.application.interfaces.g_Formation.Formation.IFormationRepositoryService;
import com.example.BacK.domain.g_Formation.Formation;
import com.example.BacK.infrastructure.repository.g_Formation.FormationRepository;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class FormationRepositoryService implements IFormationRepositoryService {

    private final FormationRepository formationRepository;

    public FormationRepositoryService(FormationRepository formationRepository) {
        this.formationRepository = formationRepository;
    }


    @Override
    public Formation saveFormation(Formation formation) {
        return formationRepository.save(formation);
    }


    @Override
    public Formation updateFormation(Long id, Formation formation) {
        return formationRepository.findById(id)
                .map(existing -> {
                    existing.setTheme(formation.getTheme());
                    existing.setDescriptionTheme(formation.getDescriptionTheme());
                    existing.setObjectifsGlobaux(formation.getObjectifsGlobaux());
                    existing.setNombreHeures(formation.getNombreHeures());
                    existing.setPrix(formation.getPrix());
                    existing.setNombreMax(formation.getNombreMax());
                    existing.setPopulationCible(formation.getPopulationCible());
                    existing.setTypeFormation(formation.getTypeFormation());
                    existing.setNiveau(formation.getNiveau());
                    existing.setStatut(formation.getStatut());
                    existing.setDomaine(formation.getDomaine());
                    existing.setType(formation.getType());
                    existing.setCategorie(formation.getCategorie());
                    existing.setSousCategorie(formation.getSousCategorie());

                    return formationRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Formation non trouvée avec l'ID : " + id));
    }


    @Override
    public void deleteFormation(Long id) {
        if (!formationRepository.existsById(id)) {
            throw new RuntimeException("Formation non trouvée avec l'ID : " + id);
        }
        formationRepository.deleteById(id);
    }


    @Override
    @Transactional(readOnly = true)
    public Optional<Formation> getFormationById(Long id) {
        Optional<Formation> formationOpt = formationRepository.findByIdWithProgrammes(id);
        
        if (formationOpt.isPresent()) {
            Formation formation = formationOpt.get();
            
            if (formation.getProgrammesDetailes() != null) {
                formation.getProgrammesDetailes().forEach(prog -> {
                    if (prog.getJoursFormation() != null) {
                        Hibernate.initialize(prog.getJoursFormation());
                        prog.getJoursFormation().forEach(jour -> {
                            if (jour.getContenusDetailles() != null) {
                                Hibernate.initialize(jour.getContenusDetailles());
                                // Initialize levels and files for each contenu detaille
                                jour.getContenusDetailles().forEach(contenu -> {
                                    if (contenu.getLevels() != null) {
                                        Hibernate.initialize(contenu.getLevels());
                                        contenu.getLevels().forEach(level -> {
                                            if (level.getFiles() != null) {
                                                Hibernate.initialize(level.getFiles());
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                });
            }
            
            // Fetch objectifs with their content
            formationRepository.findByIdWithObjectifs(id);
            
            // Initialize ObjectifsGlobaux collection
            if (formation.getObjectifsGlobaux() != null) {
                Hibernate.initialize(formation.getObjectifsGlobaux());
            }
            
            // Initialize content for each ObjectifSpecifique
            if (formation.getObjectifsSpecifiques() != null) {
                formation.getObjectifsSpecifiques().forEach(objectif -> {
                    try {
                        Hibernate.initialize(objectif.getObjectifGlobal());
                    } catch (Exception e) {
                    }
                    
                    if (objectif.getContenusJourFormation() != null) {
                        Hibernate.initialize(objectif.getContenusJourFormation());
                        objectif.getContenusJourFormation().forEach(contenu -> {
                            if (contenu.getContenusDetailles() != null) {
                                Hibernate.initialize(contenu.getContenusDetailles());
                            }
                            // Initialize contenuAssignments to get assigned ContenuDetaille IDs and their hours from ContentLevel
                            if (contenu.getContenuAssignments() != null) {
                                Hibernate.initialize(contenu.getContenuAssignments());
                                contenu.getContenuAssignments().forEach(assignment -> {
                                    if (assignment.getContenuDetaille() != null) {
                                        Hibernate.initialize(assignment.getContenuDetaille());
                                        // Initialize levels to access hours (dureeTheorique/dureePratique)
                                        var cd = assignment.getContenuDetaille();
                                        if (cd.getLevels() != null) {
                                            Hibernate.initialize(cd.getLevels());
                                        }
                                    }
                                });
                            }
                        });
                    }
                });
            }
        }
        
        return formationOpt;
    }


    @Override
    public List<Formation> getAllFormations() {
        return formationRepository.findAll();
    }


    @Override
    public boolean existsById(Long id) {
        return formationRepository.existsById(id);
    }
}

