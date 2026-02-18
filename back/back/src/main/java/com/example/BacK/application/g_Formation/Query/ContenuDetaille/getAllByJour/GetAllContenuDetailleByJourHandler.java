package com.example.BacK.application.g_Formation.Query.ContenuDetaille.getAllByJour;

import com.example.BacK.application.g_Formation.Query.ContenuDetaille.ContentLevelResponseDto;
import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Formation.ContenuDetaille;
import com.example.BacK.domain.g_Formation.ContentLevel;
import com.example.BacK.infrastructure.services.g_Formation.ContenuDetailleRepositoryService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component("GetAllContenuDetailleByJourHandler")
public class GetAllContenuDetailleByJourHandler implements RequestHandler<GetAllContenuDetailleByJourQuery, List<GetAllContenuDetailleByJourResponse>> {

    private final ContenuDetailleRepositoryService contenuDetailleRepositoryService;

    public GetAllContenuDetailleByJourHandler(
            ContenuDetailleRepositoryService contenuDetailleRepositoryService) {
        this.contenuDetailleRepositoryService = contenuDetailleRepositoryService;
    }

    @Override
    public List<GetAllContenuDetailleByJourResponse> handle(GetAllContenuDetailleByJourQuery query) {
        List<ContenuDetaille> contenus = contenuDetailleRepositoryService
                .getContenuDetailleByJour(query.getIdJourFormation());

        return contenus.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private GetAllContenuDetailleByJourResponse mapToResponse(ContenuDetaille contenuDetaille) {
        GetAllContenuDetailleByJourResponse response = new GetAllContenuDetailleByJourResponse();
        response.setIdContenuDetaille(contenuDetaille.getIdContenuDetaille());
        response.setTitre(contenuDetaille.getTitre());
        response.setContenusCles(contenuDetaille.getContenusCles());
        response.setMethodesPedagogiques(contenuDetaille.getMethodesPedagogiques());
        response.setDureeTheorique(contenuDetaille.getDureeTheorique());
        response.setDureePratique(contenuDetaille.getDureePratique());
        response.setTags(contenuDetaille.getTags());
        
        // Manually map levels with files
        List<ContentLevelResponseDto> levelDtos = new ArrayList<>();
        if (contenuDetaille.getLevels() != null) {
            for (ContentLevel level : contenuDetaille.getLevels()) {
                ContentLevelResponseDto dto = new ContentLevelResponseDto();
                dto.setId(level.getId());
                dto.setLevelNumber(level.getLevelNumber());
                dto.setTheorieContent(level.getTheorieContent());
                dto.setDureeTheorique(level.getDureeTheorique());
                dto.setDureePratique(level.getDureePratique());
                dto.setFiles(level.getFiles() != null ? level.getFiles() : new ArrayList<>());
                levelDtos.add(dto);
            }
        }
        response.setLevels(levelDtos);
        
        return response;
    }
}
