package com.example.BacK.application.g_Projet.Query.Tache.ById;


import com.example.BacK.application.interfaces.g_Projet.tache.ITacheRepositoryService;
import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.application.models.g_projet.*;
import com.example.BacK.application.models.g_rh.EmployeeDTO;
import com.example.BacK.domain.g_Projet.Mission;
import com.example.BacK.domain.g_Projet.Tache;
import com.example.BacK.domain.g_RH.Employee;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component("GetTacheByIDHandler")
public class GetTacheByIDHandler implements RequestHandler<GetTacheByIDQuery, GetTacheByIDResponse> {
    private final ITacheRepositoryService tacheRepositoryService;

    public GetTacheByIDHandler(ITacheRepositoryService tacheRepositoryService) {
        this.tacheRepositoryService = tacheRepositoryService;
    }

    @Override
    public GetTacheByIDResponse handle(GetTacheByIDQuery command) {
        // Récupérer la tâche
        Tache tache = tacheRepositoryService.get(command.getId());

        // Mapper l'entité vers le DTO
        GetTacheByIDResponse response = mapToDto(tache);

        // Retourner sous forme de liste
        return response;
    }

    public GetTacheByIDResponse mapToDto(Tache tache) {
        if (tache == null) return null;

        GetTacheByIDResponse dto = new GetTacheByIDResponse();
        dto.setId(tache.getId());
        dto.setNom(tache.getNom());
        dto.setDescription(tache.getDescription());
        dto.setStatut(tache.getStatut());
        dto.setPriorite(tache.getPriorite());
        dto.setDateDebut(tache.getDateDebut());
        dto.setDateFin(tache.getDateFin());
        dto.setDureeEstimee(tache.getDureeEstimee());
        dto.setDureeReelle(tache.getDureeReelle());
        dto.setProgression(tache.getProgression());

        // Mapper les relations ManyToOne
        if (tache.getMission() != null) {
            Mission mission = tache.getMission();
            MissionDTO missionDto = new MissionDTO();
            missionDto.setId(mission.getId());
            missionDto.setNom(mission.getNom());
            // ajouter d'autres champs nécessaires
            dto.setMission(missionDto);
        }

        if (tache.getEmployee() != null) {
            Employee emp = tache.getEmployee();
            EmployeeDTO empDto = new EmployeeDTO();
            empDto.setId(emp.getId());
            empDto.setNom(emp.getNom());
            empDto.setPrenom(emp.getPrenom());
            // ajouter d'autres champs nécessaires
            dto.setEmployee(empDto);
        }

        // Mapper les collections OneToMany
        if (tache.getCommentaires() != null) {
            List<CommentaireTacheDTO> commentairesDto = tache.getCommentaires().stream()
                    .map(c -> {
                        CommentaireTacheDTO cDto = new CommentaireTacheDTO();
                        cDto.setId(c.getId());
                        cDto.setContenu(c.getContenu());
                        // ajouter d'autres champs si nécessaire
                        return cDto;
                    })
                    .collect(Collectors.toList());
            dto.setCommentaires(commentairesDto);
        }

        if (tache.getCharges() != null) {
            List<ChargeDTO> chargesDto = tache.getCharges().stream()
                    .map(ch -> {
                        ChargeDTO chDto = new ChargeDTO();
                        chDto.setId(ch.getId());
                        chDto.setNom(ch.getNom());
                        chDto.setMontant(ch.getMontant());
                        chDto.setDescription(ch.getDescription());
                        // ajouter d'autres champs si nécessaire
                        return chDto;
                    })
                    .collect(Collectors.toList());
            dto.setCharges(chargesDto);
        }

        // Mapper les problèmes OneToMany
        if (tache.getProbleme() != null) {
            List<ProblemeTacheDTO> problemesDto = tache.getProbleme().stream()
                    .map(p -> {
                        ProblemeTacheDTO pDto = new ProblemeTacheDTO();
                        pDto.setId(p.getId());
                        pDto.setNom(p.getNom());
                        pDto.setDescription(p.getDescription());

                        // ajoute d'autres champs si nécessaire
                        return pDto;
                    })
                    .collect(Collectors.toList());
            dto.setProblemes(problemesDto);
        }

// Mapper les livrables OneToMany
        if (tache.getLivrable() != null) {
            List<LivrableTacheDTO> livrablesDto = tache.getLivrable().stream()
                    .map(l -> {
                        LivrableTacheDTO lDto = new LivrableTacheDTO();
                        lDto.setId(l.getId());
                        lDto.setNom(l.getNom());
                        lDto.setDescription(l.getDescription());
                        // ajoute d'autres champs si nécessaire
                        return lDto;
                    })
                    .collect(Collectors.toList());
            dto.setLivrables(livrablesDto);
        }

        return dto;
    }


}