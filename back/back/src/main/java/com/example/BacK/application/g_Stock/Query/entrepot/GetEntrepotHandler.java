package com.example.BacK.application.g_Stock.Query.entrepot;

import com.example.BacK.application.interfaces.g_Stock.entrepot.IEntrepotRepositoryService;
import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Stock.Entrepot;
import com.example.BacK.infrastructure.repository.g_Stock.StockRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component("GetEntrepotHandler")
public class GetEntrepotHandler implements RequestHandler<GetEntrepotQuery, List<GetEntrepotResponse>> {

    private final IEntrepotRepositoryService entrepotRepositoryService;
    private final StockRepository stockRepository;
    private final ModelMapper modelMapper;

    public GetEntrepotHandler(IEntrepotRepositoryService entrepotRepositoryService,
                             StockRepository stockRepository,
                             ModelMapper modelMapper) {
        this.entrepotRepositoryService = entrepotRepositoryService;
        this.stockRepository = stockRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<GetEntrepotResponse> handle(GetEntrepotQuery query) {
        List<Entrepot> entrepots;

        if (query.getId() != null && !query.getId().isEmpty()) {
            entrepots = entrepotRepositoryService.findById(query.getId())
                    .map(List::of)
                    .orElse(List.of());
        } else if (query.getSearchTerm() != null || query.getVille() != null || 
                   query.getStatut() != null || query.getEstActif() != null) {
            entrepots = entrepotRepositoryService.search(
                    query.getSearchTerm(),
                    query.getVille(),
                    query.getStatut(),
                    query.getEstActif()
            );
        } else {
            entrepots = entrepotRepositoryService.findAll();
        }

        return entrepots.stream()
                .map(this::mapToEntrepotResponse)
                .collect(Collectors.toList());
    }
    
    private GetEntrepotResponse mapToEntrepotResponse(Entrepot entrepot) {
        GetEntrepotResponse response = modelMapper.map(entrepot, GetEntrepotResponse.class);
        Long count = stockRepository.countDistinctArticlesByEntrepot(entrepot.getId());
        response.setNombreProduits(count);
        return response;
    }
}
