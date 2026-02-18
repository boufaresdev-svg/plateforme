package com.example.BacK.application.g_Formation.Query.Formateur;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.infrastructure.services.g_Formation.FormateurRepositoryService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class GetFormateurHandler implements RequestHandler<GetFormateurQuery, List<GetFormateurResponse>> {

    private final FormateurRepositoryService formateurRepositoryService;

    public GetFormateurHandler(FormateurRepositoryService formateurRepositoryService) {
        this.formateurRepositoryService = formateurRepositoryService;
    }

    @Override
    public List<GetFormateurResponse> handle(GetFormateurQuery query) {
        if (query.getIdFormateur() != null) {
            return formateurRepositoryService.getFormateurById(query.getIdFormateur())
                    .map(formateur -> List.of(
                            new GetFormateurResponse(
                                    formateur.getIdFormateur(),
                                    formateur.getNom(),
                                    formateur.getPrenom(),
                                    formateur.getSpecialite(),
                                    formateur.getContact(),
                                    formateur.getExperience(),
                                    formateur.getDocumentUrl()
                            )
                    )).orElseThrow(() -> new IllegalArgumentException("Formateur non trouvé avec l'ID : " + query.getIdFormateur()));
        }

        List<GetFormateurResponse> formateurs = formateurRepositoryService.getAllFormateurs().stream()
                .map(formateur -> new GetFormateurResponse(
                        formateur.getIdFormateur(),
                        formateur.getNom(),
                        formateur.getPrenom(),
                        formateur.getSpecialite(),
                        formateur.getContact(),
                        formateur.getExperience(),
                        formateur.getDocumentUrl()
                ))
                .collect(Collectors.toList());

        return formateurs;
    }
}

