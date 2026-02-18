package com.example.BacK.application.g_Stock.Query.marque;

import com.example.BacK.application.interfaces.g_Stock.marque.IMarqueRepositoryService;
import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Stock.Marque;
import com.example.BacK.infrastructure.repository.g_Stock.MarqueRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component("GetMarqueHandler")
public class GetMarqueHandler implements RequestHandler<GetMarqueQuery, List<GetMarqueResponse>> {

    private final IMarqueRepositoryService marqueRepositoryService;
    private final MarqueRepository marqueRepository;
    private final ModelMapper modelMapper;

    public GetMarqueHandler(IMarqueRepositoryService marqueRepositoryService, 
                           MarqueRepository marqueRepository,
                           ModelMapper modelMapper) {
        this.marqueRepositoryService = marqueRepositoryService;
        this.marqueRepository = marqueRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<GetMarqueResponse> handle(GetMarqueQuery query) {
        List<Marque> marques;

        // If ID is provided, get specific marque
        if (query.getId() != null && !query.getId().isEmpty()) {
            Marque marque = marqueRepositoryService.getById(query.getId());
            if (marque != null) {
                marques = List.of(marque);
            } else {
                marques = List.of();
            }
        }
        // Check if there are search criteria
        else if (hasSearchCriteria(query)) {
            marques = marqueRepositoryService.search(
                query.getNom(),
                query.getCodeMarque(),
                query.getPays(),
                query.getEstActif()
            );
        }
        // If no criteria, get all
        else {
            marques = marqueRepositoryService.getAll();
        }

        return marques.stream()
                .map(this::mapToMarqueResponse)
                .collect(Collectors.toList());
    }

    private boolean hasSearchCriteria(GetMarqueQuery query) {
        return (query.getNom() != null && !query.getNom().isEmpty()) ||
               (query.getCodeMarque() != null && !query.getCodeMarque().isEmpty()) ||
               (query.getPays() != null && !query.getPays().isEmpty()) ||
               query.getEstActif() != null;
    }
    
    private GetMarqueResponse mapToMarqueResponse(Marque marque) {
        GetMarqueResponse response = modelMapper.map(marque, GetMarqueResponse.class);
        Long count = marqueRepository.countArticlesByMarque(marque.getId());
        response.setNombreProduits(count);
        return response;
    }
}
