package com.example.BacK.infrastructure.services.g_Projet;

import com.example.BacK.application.g_Projet.Query.projet.all.GetProjetResponse;
import com.example.BacK.application.interfaces.g_Projet.projet.IProjetRepositoryService;
import com.example.BacK.application.models.g_Client.ClientDTO;
import com.example.BacK.application.models.g_Client.FactureClientDTO;
import com.example.BacK.application.models.g_projet.MissionDTO;
import com.example.BacK.application.models.g_projet.PhaseDTO;
import com.example.BacK.domain.g_Projet.Projet;
import com.example.BacK.infrastructure.repository.g_Projet.ProjectRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectRepositoryService implements IProjetRepositoryService {

    private final ProjectRepository projectRepository;
    private final ModelMapper mapper;

    public ProjectRepositoryService(ProjectRepository projectRepository, ModelMapper mapper) {
        this.projectRepository = projectRepository;
        this.mapper = mapper;
    }

    @Override
    public String add(Projet project) {
        project.setId(null); // ID null pour création
        projectRepository.save(project);
        return "ok";
    }

    @Override
    public void update(Projet project) {
        if (!projectRepository.existsById(project.getId())) {
            throw new IllegalArgumentException("Project ID not found");
        }
        projectRepository.save(project);
    }

    @Override
    public void delete(String id) {
        projectRepository.deleteById(id);
    }

    @Override
    public Projet get(String id) {
        return    projectRepository.findById(id).orElse(null);

    }

    @Override
    public List<GetProjetResponse> getall() {
        List<Projet> projets = projectRepository.findAll();

        return projets.stream()
                .map(this::mapToGetProjetResponse)
                .toList();
    }

    private GetProjetResponse mapToGetProjetResponse(Projet projet) {

        GetProjetResponse dto = new GetProjetResponse();

        dto.setId(projet.getId());
        dto.setNom(projet.getNom());
        dto.setDescription(projet.getDescription());
        dto.setType(projet.getType());
        dto.setStatut(projet.getStatut());
        dto.setPriorite(projet.getPriorite());
        dto.setChefProjet(projet.getChefProjet());
        dto.setDateDebut(projet.getDateDebut());
        dto.setDateFin(projet.getDateFin());
        dto.setDateFinPrevue(projet.getDateFinPrevue());
        dto.setBudget(projet.getBudget());
        dto.setCoutReel(projet.getCoutReel());
        dto.setProgression(projet.getProgression());
        dto.setDocuments(projet.getDocuments());

        // 🔹 Client
        if (projet.getClient() != null) {
            ClientDTO clientDTO = new ClientDTO();
            clientDTO.setId(projet.getClient().getId());
            clientDTO.setNom(projet.getClient().getNom());
            dto.setClient(clientDTO);
        }

        // 🔹 Missions
        dto.setMissions(
                projet.getMissions().stream().map(m -> {
                    MissionDTO missionDTO = new MissionDTO();
                    missionDTO.setId(m.getId());
                    missionDTO.setNom(m.getNom());
                    missionDTO.setDescription(m.getDescription());
                    missionDTO.setStatut(m.getStatut());
                    missionDTO.setDateDebut(m.getDateDebut());
                    missionDTO.setDateFin(m.getDateFin());
                    return missionDTO;
                }).toList()
        );

        // 🔹 Phases
        dto.setPhases(
                projet.getPhases().stream().map(ph -> {
                    PhaseDTO phaseDTO = new PhaseDTO();
                    phaseDTO.setId(ph.getId());
                    phaseDTO.setNom(ph.getNom());
                    phaseDTO.setDescription(ph.getDescription());
                    phaseDTO.setDateDebut(ph.getDateDebut());
                    phaseDTO.setDateFin(ph.getDateFin());
                    return phaseDTO;
                }).toList()
        );

        // 🔹 Factures
        dto.setFactures(
                projet.getFactures().stream().map(f -> {
                    FactureClientDTO factureDTO = new FactureClientDTO();
                    factureDTO.setId(f.getId());
                    factureDTO.setMontant(f.getMontant());
                    factureDTO.setDateEmission(f.getDateEmission());
                    return factureDTO;
                }).toList()
        );

        return dto;
    }

}

