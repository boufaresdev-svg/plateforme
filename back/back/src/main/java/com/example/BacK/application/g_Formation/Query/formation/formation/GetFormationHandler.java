package com.example.BacK.application.g_Formation.Query.formation.formation;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.application.models.g_formation.CategorieDTO;
import com.example.BacK.application.models.g_formation.ContenuJourFormationDTO;
import com.example.BacK.application.models.g_formation.DomaineDTO;
import com.example.BacK.application.models.g_formation.ObjectifGlobalDTO;
import com.example.BacK.application.models.g_formation.ObjectifSpecifiqueDTO;
import com.example.BacK.application.models.g_formation.SousCategorieDTO;
import com.example.BacK.application.models.g_formation.TypeDTO;
import com.example.BacK.domain.g_Formation.*;
import com.example.BacK.infrastructure.services.g_Formation.FormationRepositoryService;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component("GetFormationHandler")
public class GetFormationHandler implements RequestHandler<GetFormationQuery, List<GetFormationResponse>> {

    private final FormationRepositoryService formationRepositoryService;

    public GetFormationHandler(FormationRepositoryService formationRepositoryService) {
        this.formationRepositoryService = formationRepositoryService;
    }

    @Override
    public List<GetFormationResponse> handle(GetFormationQuery command) {

        if (command.getIdFormation() != null) {
            Formation formation = formationRepositoryService.getFormationById(command.getIdFormation())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Formation non trouvée avec l'ID : " + command.getIdFormation()
                    ));

            return List.of(mapToResponse(formation));
        }

        return formationRepositoryService.getAllFormations()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private GetFormationResponse mapToResponse(Formation formation) {
        GetFormationResponse response = new GetFormationResponse();

        response.setIdFormation(formation.getIdFormation());
        response.setTheme(formation.getTheme());
        response.setDescriptionTheme(formation.getDescriptionTheme());
        response.setObjectifSpecifiqueGlobal(formation.getObjectifSpecifiqueGlobal());
        
        // Map ObjectifGlobal list to DTOs
        if (formation.getObjectifsGlobaux() != null) {
            List<ObjectifGlobalDTO> objectifsDTO = formation.getObjectifsGlobaux()
                .stream()
                .map(og -> new ObjectifGlobalDTO(
                    og.getIdObjectifGlobal(),
                    og.getLibelle(),
                    og.getDescription(),
                    og.getTags()
                ))
                .collect(Collectors.toList());
            response.setObjectifsGlobaux(objectifsDTO);
        }
        
        // Map ObjectifSpecifique list to DTOs with their content
        if (formation.getObjectifsSpecifiques() != null) {
            List<ObjectifSpecifiqueDTO> objectifsSpecDTO = formation.getObjectifsSpecifiques()
                .stream()
                .map(os -> {
                    ObjectifSpecifiqueDTO dto = new ObjectifSpecifiqueDTO(
                        os.getIdObjectifSpec(),
                        os.getTitre(),
                        os.getDescription()
                    );
                    
                    // Set the parent global objective ID
                    if (os.getObjectifGlobal() != null) {
                        dto.setIdObjectifGlobal(os.getObjectifGlobal().getIdObjectifGlobal());
                    }
                    
                    // Map contenusJourFormation for each objectif
                    if (os.getContenusJourFormation() != null && !os.getContenusJourFormation().isEmpty()) {
                        List<ContenuJourFormationDTO> contenusDTO = os.getContenusJourFormation()
                            .stream()
                            .map(contenu -> {
                                // Get assigned ContenuDetaille IDs and sum hours from ContentLevel
                                List<Long> assignedIds = null;
                                int sumTheorique = 0;
                                int sumPratique = 0;
                                
                                if (contenu.getContenuAssignments() != null && !contenu.getContenuAssignments().isEmpty()) {
                                    assignedIds = new java.util.ArrayList<>();
                                    for (var assignment : contenu.getContenuAssignments()) {
                                        if (assignment.getContenuDetaille() != null) {
                                            var cd = assignment.getContenuDetaille();
                                            assignedIds.add(cd.getIdContenuDetaille());
                                            
                                            // Sum hours from ContentLevel (support de cours)
                                            if (cd.getLevels() != null && !cd.getLevels().isEmpty()) {
                                                for (var level : cd.getLevels()) {
                                                    sumTheorique += level.getDureeTheorique() != null ? level.getDureeTheorique().intValue() : 0;
                                                    sumPratique += level.getDureePratique() != null ? level.getDureePratique().intValue() : 0;
                                                }
                                            }
                                        }
                                    }
                                }
                                
                                // Use summed hours from ContentLevel, fallback to direct ContenuJourFormation hours
                                int nbHeuresTheo = sumTheorique > 0 ? sumTheorique : contenu.getNbHeuresTheoriques();
                                int nbHeuresPrat = sumPratique > 0 ? sumPratique : contenu.getNbHeuresPratiques();
                                
                                return new ContenuJourFormationDTO(
                                    contenu.getIdContenuJour(),
                                    contenu.getContenu(),
                                    contenu.getMoyenPedagogique(),
                                    contenu.getSupportPedagogique(),
                                    nbHeuresTheo,
                                    nbHeuresPrat,
                                    contenu.getNumeroJour(),
                                    contenu.getStaff(),
                                    contenu.getNiveau(),
                                    contenu.getObjectifSpecifique() != null ? contenu.getObjectifSpecifique().getIdObjectifSpec() : null,
                                    contenu.getPlanFormation() != null ? contenu.getPlanFormation().getIdPlanFormation() : null,
                                    assignedIds
                                );
                            })
                            .collect(Collectors.toList());
                        dto.setContenus(contenusDTO);
                    }
                    
                    return dto;
                })
                .collect(Collectors.toList());
            response.setObjectifsSpecifiques(objectifsSpecDTO);
        }
        
        response.setTags(formation.getTags());
        response.setNombreHeures(formation.getNombreHeures());
        response.setPrix(formation.getPrix());
        response.setNombreMax(formation.getNombreMax());
        response.setPopulationCible(formation.getPopulationCible());
        response.setImageUrl(formation.getImageUrl());
        response.setTypeFormation(formation.getTypeFormation());
        response.setNiveau(formation.getNiveau());

        /* ===== DOMAINE ===== */
        if (formation.getDomaine() != null) {
            Domaine d = formation.getDomaine();
            response.setDomaine(new DomaineDTO(
                    d.getIdDomaine(),
                    d.getNom(),
                    d.getDescription()
            ));
            response.setIdDomaine(d.getIdDomaine());
        }

        /* ===== TYPE ===== */
        if (formation.getType() != null) {
            Type t = formation.getType();
            response.setType(new TypeDTO(
                    t.getIdType(),
                    t.getNom(),
                    t.getDescription(),
                    t.getDomaine().getIdDomaine()
            ));
            response.setIdType(t.getIdType());
        }

        if (formation.getCategorie() != null) {
            Categorie c = formation.getCategorie();
            response.setCategorie(new CategorieDTO(
                    c.getIdCategorie(),
                    c.getNom(),
                    c.getDescription(),
                    c.getType().getIdType()
            ));
            response.setIdCategorie(c.getIdCategorie());
        }

        if (formation.getSousCategorie() != null) {
            SousCategorie sc = formation.getSousCategorie();
            response.setSousCategorie(new SousCategorieDTO(
                    sc.getIdSousCategorie(),
                    sc.getNom(),
                    sc.getDescription()
            ));
            response.setIdSousCategorie(sc.getIdSousCategorie());
        }

        // Map ProgrammeDetaile with nested Jours and Contenus
        if (formation.getProgrammesDetailes() != null && !formation.getProgrammesDetailes().isEmpty()) {
            List<GetFormationResponse.ProgrammeDetaileDTO> programmes = formation.getProgrammesDetailes()
                .stream()
                .map(prog -> {
                    GetFormationResponse.ProgrammeDetaileDTO progDTO = new GetFormationResponse.ProgrammeDetaileDTO();
                    progDTO.setIdProgramme(prog.getIdProgramme());
                    progDTO.setTitre(prog.getTitre());
                    
                    if (prog.getJoursFormation() != null && !prog.getJoursFormation().isEmpty()) {
                        List<GetFormationResponse.JourFormationDTO> jours = prog.getJoursFormation()
                            .stream()
                            .map(jour -> {
                                GetFormationResponse.JourFormationDTO jourDTO = new GetFormationResponse.JourFormationDTO();
                                jourDTO.setIdJour(jour.getIdJour());
                                jourDTO.setNumeroJour(jour.getNumeroJour());
                                
                                if (jour.getContenusDetailles() != null && !jour.getContenusDetailles().isEmpty()) {
                                    List<GetFormationResponse.ContenuDetailleDTO> contenus = jour.getContenusDetailles()
                                        .stream()
                                        .map(contenu -> {
                                            // Sum hours from ContentLevel (support de cours files)
                                            Double totalTheorique = 0.0;
                                            Double totalPratique = 0.0;
                                            
                                            if (contenu.getLevels() != null && !contenu.getLevels().isEmpty()) {
                                                for (var level : contenu.getLevels()) {
                                                    totalTheorique += level.getDureeTheorique() != null ? level.getDureeTheorique() : 0.0;
                                                    totalPratique += level.getDureePratique() != null ? level.getDureePratique() : 0.0;
                                                }
                                            }
                                            
                                            return new GetFormationResponse.ContenuDetailleDTO(
                                                contenu.getIdContenuDetaille(),
                                                contenu.getContenusCles(),
                                                contenu.getMethodesPedagogiques(),
                                                totalTheorique,
                                                totalPratique
                                            );
                                        })
                                        .collect(Collectors.toList());
                                    jourDTO.setContenus(contenus);
                                }
                                return jourDTO;
                            })
                            .collect(Collectors.toList());
                        progDTO.setJours(jours);
                    }
                    return progDTO;
                })
                .collect(Collectors.toList());
            response.setProgrammesDetailes(programmes);
        }

        return response;
    }
}
