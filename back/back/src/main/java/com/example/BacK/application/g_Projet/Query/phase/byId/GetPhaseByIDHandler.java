package com.example.BacK.application.g_Projet.Query.phase.byId;

import com.example.BacK.application.interfaces.g_Projet.phase.IPhaseRespositoryService;
import com.example.BacK.application.interfaces.g_Projet.projet.IProjetRepositoryService;
import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.application.models.g_Client.ClientDTO;
import com.example.BacK.application.models.g_projet.MissionDTO;
import com.example.BacK.application.models.g_projet.PhaseDTO;
import com.example.BacK.application.models.g_projet.ProjetDTO;
import com.example.BacK.domain.g_Projet.Phase;
import com.example.BacK.domain.g_Projet.Projet;
import com.example.BacK.domain.g_Projet.enumEntity.StatutProjet;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component("GetPhaseByIDHandler")
public class GetPhaseByIDHandler implements RequestHandler<GetPhaseByIDQuery,List<GetPhaseByIDResponse>> {
    private  final IPhaseRespositoryService _PhaseRespositoryService;
    private final ModelMapper _modelMapper;

    public GetPhaseByIDHandler(IPhaseRespositoryService _PhaseRespositoryService, ModelMapper _modelMapper) {
        this._PhaseRespositoryService = _PhaseRespositoryService;
        this._modelMapper = _modelMapper;
    }

    @Override
    public List<GetPhaseByIDResponse> handle(GetPhaseByIDQuery query) {
        Phase phase = _PhaseRespositoryService.get(query.getId());
        GetPhaseByIDResponse response = mapPhaseToResponse(phase);
        return List.of(response);
    }

    public GetPhaseByIDResponse mapPhaseToResponse(Phase phase) {
        if (phase == null) return null;

        GetPhaseByIDResponse response = new GetPhaseByIDResponse();
        response.setId(phase.getId());
        response.setNom(phase.getNom());
        response.setDescription(phase.getDescription());
        response.setOrdre(phase.getOrdre());
        response.setStatut(phase.getStatut());
        response.setDateDebut(phase.getDateDebut());
        response.setDateFin(phase.getDateFin());
        response.setProgression(phase.getProgression());
        response.setBudget(phase.getBudget());

        // 🔹 Mapping du projet associé
        if (phase.getProjet() != null) {
            ProjetDTO projetDTO = new ProjetDTO();
            projetDTO.setId(phase.getProjet().getId());
            projetDTO.setNom(phase.getProjet().getNom());
            projetDTO.setDescription(phase.getProjet().getDescription());
            projetDTO.setType(phase.getProjet().getType());
            projetDTO.setPriorite(phase.getProjet().getPriorite());
            projetDTO.setChefProjet(phase.getProjet().getChefProjet());
            projetDTO.setDateDebut(phase.getProjet().getDateDebut());
            projetDTO.setDateFin(phase.getProjet().getDateFin());
            projetDTO.setBudget(phase.getProjet().getBudget());
            response.setProjet(projetDTO);
        }

        // 🔹 Mapping des missions associées à la phase
        if (phase.getMissions() != null) {
            List<MissionDTO> missionDTOs = phase.getMissions().stream().map(mission -> {
                MissionDTO dto = new MissionDTO();
                dto.setId(mission.getId());
                dto.setNom(mission.getNom());
                dto.setDescription(mission.getDescription());
                dto.setStatut(mission.getStatut());
                dto.setDateDebut(mission.getDateDebut());
                dto.setDateFin(mission.getDateFin());
                return dto;
            }).collect(Collectors.toList());
            response.setMissions(missionDTOs);
        }

        // 🔹 Livrables (si tu les stockes ailleurs)
        response.setLivrables(new ArrayList<>());

        return response;
    }


}

