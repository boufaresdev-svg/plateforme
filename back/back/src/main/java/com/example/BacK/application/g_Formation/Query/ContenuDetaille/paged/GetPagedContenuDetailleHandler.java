package com.example.BacK.application.g_Formation.Query.ContenuDetaille.paged;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Formation.ContenuDetaille;
import com.example.BacK.infrastructure.repository.g_Formation.ContenuDetailleRepository;
import com.example.BacK.infrastructure.services.g_Formation.ContenuDetailleRepositoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component("GetPagedContenuDetailleHandler")
public class GetPagedContenuDetailleHandler implements RequestHandler<GetPagedContenuDetailleQuery, PagedContenuDetailleResponse> {

    private final ContenuDetailleRepository contenuDetailleRepository;
    private final ContenuDetailleRepositoryService contenuDetailleRepositoryService;

    public GetPagedContenuDetailleHandler(ContenuDetailleRepository contenuDetailleRepository,
                                          ContenuDetailleRepositoryService contenuDetailleRepositoryService) {
        this.contenuDetailleRepository = contenuDetailleRepository;
        this.contenuDetailleRepositoryService = contenuDetailleRepositoryService;
    }

    @Override
    public PagedContenuDetailleResponse handle(GetPagedContenuDetailleQuery query) {
        // Determine sort direction
        Sort.Direction direction = "DESC".equalsIgnoreCase(query.getSortDirection()) 
            ? Sort.Direction.DESC 
            : Sort.Direction.ASC;
        
        // Create pageable
        Pageable pageable = PageRequest.of(
            query.getPage(), 
            query.getSize(), 
            Sort.by(direction, query.getSortBy())
        );
        
        // Fetch paginated contenus with optional jour filter
        Page<ContenuDetaille> contenus;
        if (query.getIdJourFormation() != null) {
            contenus = contenuDetailleRepository.findByJourFormation_IdJour(query.getIdJourFormation(), pageable);
        } else {
            contenus = contenuDetailleRepository.findAll(pageable);
        }

        // Fallback: if database page is empty but we do have data in full list, paginate manually
        if (contenus.isEmpty()) {
            var fullList = contenuDetailleRepositoryService.getAllContenuDetaille();
            int total = fullList.size();
            if (total > 0) {
                int start = Math.min(query.getPage() * query.getSize(), total);
                int end = Math.min(start + query.getSize(), total);
                var subList = fullList.subList(start, end);
                var fallbackContent = subList.stream()
                        .map(this::convertToSummary)
                        .toList();
                int totalPages = (int) Math.ceil((double) total / query.getSize());
                return new PagedContenuDetailleResponse(
                        fallbackContent,
                        query.getPage(),
                        query.getSize(),
                        total,
                        totalPages,
                        query.getPage() < totalPages - 1,
                        query.getPage() > 0
                );
            }
        }
        
        // Convert to summary DTOs
        var content = contenus.getContent().stream()
            .map(this::convertToSummary)
            .toList();
        
        // Build paged response
        return new PagedContenuDetailleResponse(
            content,
            contenus.getNumber(),
            contenus.getSize(),
            contenus.getTotalElements(),
            contenus.getTotalPages(),
            contenus.hasNext(),
            contenus.hasPrevious()
        );
    }
    
    private PagedContenuDetailleResponse.ContenuSummary convertToSummary(ContenuDetaille contenu) {
        return new PagedContenuDetailleResponse.ContenuSummary(
            contenu.getIdContenuDetaille(),
            contenu.getTitre(),
            contenu.getMethodesPedagogiques(),
            contenu.getDureeTheorique(),
            contenu.getDureePratique(),
            contenu.getLevels() != null ? contenu.getLevels().size() : 0
        );
    }
}
