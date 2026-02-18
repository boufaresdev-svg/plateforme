package com.example.BacK.application.g_Projet.Query.projet.byId;

import com.example.BacK.application.interfaces.g_Projet.projet.IProjetRepositoryService;
import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.application.models.g_Client.ClientDTO;
import com.example.BacK.application.models.g_projet.*;
import com.example.BacK.application.models.g_rh.EmployeeDTO;
import com.example.BacK.domain.g_Client.Client;
import com.example.BacK.domain.g_Projet.Projet;
import com.example.BacK.domain.g_RH.Employee;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component("GetProjetByIDHandler")
public class GetProjetByIDHandler implements RequestHandler<GetProjetByIDQuery, List<GetProjetByIDResponse>> {
    private final IProjetRepositoryService _projetRepositoryService;
    private final ModelMapper _modelMapper;

    public GetProjetByIDHandler(IProjetRepositoryService projetRepositoryService, ModelMapper _modelMapper) {
        this._projetRepositoryService = projetRepositoryService;
        this._modelMapper = _modelMapper;
    }

    @Override
    public List<GetProjetByIDResponse> handle(GetProjetByIDQuery query) {
        Projet projet = _projetRepositoryService.get(query.getId());
        GetProjetByIDResponse response = mapProjetToResponse(projet);
        return List.of(response);
    }

    public GetProjetByIDResponse mapProjetToResponse(Projet projet) {
        if (projet == null) return null;

        GetProjetByIDResponse response = new GetProjetByIDResponse();
        response.setId(projet.getId());
        response.setNom(projet.getNom());
        response.setDescription(projet.getDescription());
        response.setType(projet.getType());
        response.setStatut(String.valueOf(projet.getStatut()));
        response.setPriorite(projet.getPriorite());
        response.setChefProjet(projet.getChefProjet());
        response.setDateDebut(projet.getDateDebut());
        response.setDateFin(projet.getDateFin());
        response.setDateFinPrevue(projet.getDateFinPrevue());
        response.setBudget(projet.getBudget());
        response.setCoutReel(projet.getCoutReel());
        response.setProgression(projet.getProgression());

        // 🔹 Client
        if (projet.getClient() != null) {
            Client c = projet.getClient();
            ClientDTO clientDTO = new ClientDTO();
            clientDTO.setId(c.getId());
            clientDTO.setNom(c.getNom());
            clientDTO.setPrenom(c.getPrenom());
            clientDTO.setRaisonSociale(c.getRaisonSociale());
            clientDTO.setType(c.getType());
            clientDTO.setSecteur(c.getSecteur());
            clientDTO.setStatut(c.getStatut());
            clientDTO.setAdresse(c.getAdresse());
            clientDTO.setVille(c.getVille());
            clientDTO.setCodePostal(c.getCodePostal());
            clientDTO.setPays(c.getPays());
            clientDTO.setTelephone(c.getTelephone());
            clientDTO.setEmail(c.getEmail());
            clientDTO.setSiteWeb(c.getSiteWeb());
            clientDTO.setLocalisation(c.getLocalisation());
            clientDTO.setIdentifiantFiscal(c.getIdentifiantFiscal());
            clientDTO.setRib(c.getRib());
            clientDTO.setPointsFidelite(c.getPointsFidelite());
            clientDTO.setChiffreAffaires(c.getChiffreAffaires());
            response.setClient(clientDTO);
        }

        // 🔹 Phases avec missions et leurs sous-éléments
        if (projet.getPhases() != null) {
            List<PhaseDTO> phaseDTOs = projet.getPhases().stream().map(phase -> {
                PhaseDTO phaseDTO = new PhaseDTO();
                phaseDTO.setId(phase.getId());
                phaseDTO.setNom(phase.getNom());
                phaseDTO.setDescription(phase.getDescription());
                phaseDTO.setOrdre(phase.getOrdre());
                phaseDTO.setStatut(phase.getStatut());
                phaseDTO.setDateDebut(phase.getDateDebut());
                phaseDTO.setDateFin(phase.getDateFin());
                phaseDTO.setProgression(phase.getProgression());
                phaseDTO.setBudget(phase.getBudget());

                // 🔹 Filtrer les missions liées à cette phase
                if (projet.getMissions() != null) {
                    List<MissionDTO> missionsPhase = projet.getMissions().stream()
                            .filter(m -> m.getPhase() != null && m.getPhase().getId().equals(phase.getId()))
                            .map(mission -> {
                                MissionDTO missionDTO = new MissionDTO();
                                missionDTO.setId(mission.getId());
                                missionDTO.setNom(mission.getNom());
                                missionDTO.setDescription(mission.getDescription());
                                missionDTO.setObjectif(mission.getObjectif());
                                missionDTO.setStatut(mission.getStatut());
                                missionDTO.setPriorite(mission.getPriorite());
                                missionDTO.setDateDebut(mission.getDateDebut());
                                missionDTO.setDateFin(mission.getDateFin());
                                missionDTO.setProgression(mission.getProgression());
                                missionDTO.setBudget(mission.getBudget());

                                // 🔹 Tâches
                                if (mission.getTaches() != null) {
                                    List<TacheDTO> tacheDTOs = mission.getTaches().stream().map(t -> {
                                        TacheDTO tDTO = new TacheDTO();
                                        tDTO.setId(t.getId());
                                        tDTO.setNom(t.getNom());
                                        tDTO.setDescription(t.getDescription());
                                        tDTO.setStatut(t.getStatut());
                                        tDTO.setPriorite(t.getPriorite());
                                        tDTO.setDateDebut(t.getDateDebut());
                                        tDTO.setDateFin(t.getDateFin());
                                        tDTO.setDureeEstimee(t.getDureeEstimee());
                                        tDTO.setDureeReelle(t.getDureeReelle());
                                        tDTO.setProgression(t.getProgression());

                                        // 🔹 Employé
                                        if (t.getEmployee() != null) {
                                            EmployeeDTO emp = new EmployeeDTO();
                                            emp.setId(t.getEmployee().getId());
                                            emp.setNom(t.getEmployee().getNom());
                                            emp.setPrenom(t.getEmployee().getPrenom());
                                            emp.setEmail(t.getEmployee().getEmail());
                                            tDTO.setEmployee(emp);
                                        }

                                        // 🔹 Commentaires
                                        if (t.getCommentaires() != null) {
                                            List<CommentaireTacheDTO> commentairesDTO = t.getCommentaires().stream().map(c -> {
                                                CommentaireTacheDTO cDTO = new CommentaireTacheDTO();
                                                cDTO.setId(c.getId());
                                                cDTO.setContenu(c.getContenu());
                                                cDTO.setAuteur(c.getAuteur());
                                                return cDTO;
                                            }).collect(Collectors.toList());
                                            tDTO.setCommentaires(commentairesDTO);
                                        }

                                        // 🔹 Charges
                                        if (t.getCharges() != null) {
                                            List<ChargeDTO> chargesDTO = t.getCharges().stream().map(ch -> {
                                                ChargeDTO chDTO = new ChargeDTO();
                                                chDTO.setId(ch.getId());
                                                chDTO.setNom(ch.getNom());
                                                chDTO.setMontant(ch.getMontant());
                                                chDTO.setDescription(ch.getDescription());
                                                chDTO.setCategorie(ch.getCategorie());
                                                chDTO.setSousCategorie(ch.getSousCategorie());
                                                return chDTO;
                                            }).collect(Collectors.toList());
                                            tDTO.setCharges(chargesDTO);
                                        }

                                        // 🔹 Livrables
                                        if (t.getLivrable() != null) {
                                            List<LivrableTacheDTO> livrablesDTO = t.getLivrable().stream().map(l -> {
                                                LivrableTacheDTO lDTO = new LivrableTacheDTO();
                                                lDTO.setId(l.getId());
                                                lDTO.setNom(l.getNom());
                                                lDTO.setDescription(l.getDescription());
                                                 return lDTO;
                                            }).collect(Collectors.toList());
                                            tDTO.setLivrable(livrablesDTO);
                                        }

                                        // 🔹 Problèmes
                                        if (t.getProbleme() != null) {
                                            List<ProblemeTacheDTO> problemesDTO = t.getProbleme().stream().map(p -> {
                                                ProblemeTacheDTO pDTO = new ProblemeTacheDTO();
                                                pDTO.setId(p.getId());
                                                pDTO.setNom(p.getNom());
                                                pDTO.setDescription(p.getDescription());
                                                return pDTO;
                                            }).collect(Collectors.toList());
                                            tDTO.setProbleme(problemesDTO);
                                        }

                                        return tDTO;
                                    }).collect(Collectors.toList());
                                    missionDTO.setTaches(tacheDTOs);
                                }

                                return missionDTO;
                            })
                            .collect(Collectors.toList());

                    phaseDTO.setMissions(missionsPhase);
                }

                return phaseDTO;
            }).collect(Collectors.toList());

            response.setPhases(phaseDTOs);
        }

        // 🔹 Documents
        response.setDocuments(projet.getDocuments());

        return response;
    }


}

