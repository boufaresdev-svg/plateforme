package com.example.BacK.application.g_Formation.Query.ContenuDetaille.getContenuDetailleById;

import com.example.BacK.application.g_Formation.Query.ContenuDetaille.ContentLevelResponseDto;
import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Formation.ContenuDetaille;
import com.example.BacK.domain.g_Formation.ContentLevel;
import com.example.BacK.infrastructure.services.g_Formation.ContenuDetailleRepositoryService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component("GetContenuDetailleByIdHandler")
public class GetContenuDetailleByIdHandler implements RequestHandler<GetContenuDetailleByIdQuery, GetContenuDetailleByIdResponse> {

    private final ContenuDetailleRepositoryService contenuDetailleRepositoryService;

    public GetContenuDetailleByIdHandler(
            ContenuDetailleRepositoryService contenuDetailleRepositoryService) {
        this.contenuDetailleRepositoryService = contenuDetailleRepositoryService;
    }

    @Override
    public GetContenuDetailleByIdResponse handle(GetContenuDetailleByIdQuery query) {
        ContenuDetaille contenuDetaille = contenuDetailleRepositoryService
                .getContenuDetailleById(query.getIdContenuDetaille())
                .orElseThrow(() -> new IllegalArgumentException(
                        "ContenuDetaille non trouvé avec l'ID : " + query.getIdContenuDetaille()
                ));

        return mapToResponse(contenuDetaille);
    }

    private GetContenuDetailleByIdResponse mapToResponse(ContenuDetaille contenuDetaille) {
        GetContenuDetailleByIdResponse response = new GetContenuDetailleByIdResponse();
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

        if (contenuDetaille.getJourFormation() != null) {
            response.setIdJourFormation(contenuDetaille.getJourFormation().getIdJour());
        }
        return response;
    }
}
