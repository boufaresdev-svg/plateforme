package com.example.BacK.application.g_Projet.Query.mission.byId;

import com.example.BacK.application.interfaces.g_Projet.mission.IMissionRepositoryService;
import com.example.BacK.application.interfaces.g_Projet.phase.IPhaseRespositoryService;
import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.application.models.g_projet.*;
import com.example.BacK.application.models.g_rh.EmployeeDTO;
import com.example.BacK.domain.g_Projet.Mission;
import com.example.BacK.domain.g_Projet.Phase;
import com.example.BacK.domain.g_Projet.Projet;
import com.example.BacK.domain.g_RH.Employee;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component("GetMissionByIDHandler")
public class GetMissionByIDHandler implements RequestHandler<GetMissionByIDQuery,List<GetMissionByIDResponse>> {
    private  final IMissionRepositoryService _iMissionRepositoryService;
    private final ModelMapper _modelMapper;

    public GetMissionByIDHandler(IMissionRepositoryService _iMissionRepositoryService, ModelMapper _modelMapper) {
        this._iMissionRepositoryService = _iMissionRepositoryService;
        this._modelMapper = _modelMapper;
    }

    @Override
    public List<GetMissionByIDResponse> handle(GetMissionByIDQuery query) {
        Mission mission = _iMissionRepositoryService.get(query.getId());
         GetMissionByIDResponse response = mapMissionToResponse(mission);
        return List.of(response);
    }

    public GetMissionByIDResponse mapMissionToResponse(Mission mission) {
        if (mission == null) return null;

        GetMissionByIDResponse response = new GetMissionByIDResponse();
        response.setId(mission.getId());
        response.setNom(mission.getNom());
        response.setDescription(mission.getDescription());
        response.setObjectif(mission.getObjectif());
        response.setStatut(mission.getStatut());
        response.setPriorite(mission.getPriorite());
        response.setDateDebut(mission.getDateDebut());
        response.setDateFin(mission.getDateFin());
        response.setProgression(mission.getProgression());
        response.setBudget(mission.getBudget());

        // ✅ Projet associé
        if (mission.getProjet() != null) {
            Projet p = mission.getProjet();
            ProjetDTO projetDTO = new ProjetDTO();
            projetDTO.setId(p.getId());
            projetDTO.setNom(p.getNom());
            projetDTO.setDescription(p.getDescription());
            projetDTO.setType(p.getType());
            projetDTO.setPriorite(p.getPriorite());
            projetDTO.setChefProjet(p.getChefProjet());
            projetDTO.setDateDebut(p.getDateDebut());
            projetDTO.setDateFin(p.getDateFin());
            projetDTO.setBudget(p.getBudget());
            response.setProjet(projetDTO);
        }

        // ✅ Phase associée
        if (mission.getPhase() != null) {
            Phase ph = mission.getPhase();
            PhaseDTO phaseDTO = new PhaseDTO();
            phaseDTO.setId(ph.getId());
            phaseDTO.setNom(ph.getNom());
            phaseDTO.setDescription(ph.getDescription());
            phaseDTO.setOrdre(ph.getOrdre());
            phaseDTO.setStatut(ph.getStatut());
            phaseDTO.setDateDebut(ph.getDateDebut());
            phaseDTO.setDateFin(ph.getDateFin());
            phaseDTO.setProgression(ph.getProgression());
            phaseDTO.setBudget(ph.getBudget());
            response.setPhase(phaseDTO);
        }

        // ✅ Tâches + sous-éléments
        if (mission.getTaches() != null) {
            List<TacheDTO> tacheDTOs = mission.getTaches().stream().map(t -> {
                TacheDTO dto = new TacheDTO();
                dto.setId(t.getId());
                dto.setNom(t.getNom());
                dto.setDescription(t.getDescription());
                dto.setStatut(t.getStatut());
                dto.setPriorite(t.getPriorite());
                dto.setDateDebut(t.getDateDebut());
                dto.setDateFin(t.getDateFin());
                dto.setProgression(t.getProgression());
                dto.setDureeEstimee(t.getDureeEstimee());
                dto.setDureeReelle(t.getDureeReelle());

                // 👤 Employé
                if (t.getEmployee() != null) {
                    Employee e = t.getEmployee();
                    EmployeeDTO employee = new EmployeeDTO();
                    employee.setId(e.getId());
                    employee.setNom(e.getNom());
                    employee.setPrenom(e.getPrenom());
                    employee.setEmail(e.getEmail());
                    dto.setEmployee(employee);
                }

                // 💬 Commentaires
                if (t.getCommentaires() != null) {
                    dto.setCommentaires(
                            t.getCommentaires().stream().map(c -> {
                                CommentaireTacheDTO cDTO = new CommentaireTacheDTO();
                                cDTO.setId(c.getId());
                                cDTO.setContenu(c.getContenu());
                                cDTO.setAuteur(c.getAuteur());
                                return cDTO;
                            }).collect(Collectors.toList())
                    );
                }

                // 💰 Charges
                if (t.getCharges() != null) {
                    dto.setCharges(
                            t.getCharges().stream().map(ch -> {
                                ChargeDTO chDTO = new ChargeDTO();
                                chDTO.setId(ch.getId());
                                chDTO.setNom(ch.getNom());
                                chDTO.setMontant(ch.getMontant());
                                chDTO.setDescription(ch.getDescription());
                                chDTO.setCategorie(ch.getCategorie());
                                chDTO.setSousCategorie(ch.getSousCategorie());
                                return chDTO;
                            }).collect(Collectors.toList())
                    );
                }

                // 📦 Livrables
                if (t.getLivrable() != null) {
                    dto.setLivrable(
                            t.getLivrable().stream().map(l -> {
                                LivrableTacheDTO lDTO = new LivrableTacheDTO();
                                lDTO.setId(l.getId());
                                lDTO.setNom(l.getNom());
                                lDTO.setDescription(l.getDescription());
                                return lDTO;
                            }).collect(Collectors.toList())
                    );
                }

                // ⚠️ Problèmes
                if (t.getProbleme() != null) {
                    dto.setProbleme(
                            t.getProbleme().stream().map(p -> {
                                ProblemeTacheDTO pDTO = new ProblemeTacheDTO();
                                pDTO.setId(p.getId());
                                pDTO.setNom(p.getNom());
                                pDTO.setDescription(p.getDescription());
                                return pDTO;
                            }).collect(Collectors.toList())
                    );
                }

                return dto;
            }).collect(Collectors.toList());

            response.setTaches(tacheDTOs);
        }

        // ✅ Employés affectés à la mission
        if (mission.getEmployesAffectes() != null) {
            response.setEmployesAffectes(
                    mission.getEmployesAffectes().stream().map(emp -> {
                        EmployeAffecteDTO dto = new EmployeAffecteDTO();
                        dto.setId(emp.getId());
                        dto.setRole(emp.getRole());
                        dto.setEmployee(emp.getEmployee());
                        return dto;
                    }).collect(Collectors.toList())
            );
        }

        return response;
    }


}

