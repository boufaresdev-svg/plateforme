package com.example.BacK.application.g_Formation.Query.formation.formation;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Formation.Formation;
import com.example.BacK.infrastructure.repository.g_Formation.FormationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component("GetFormationPagedHandler")
public class GetFormationPagedHandler implements RequestHandler<GetFormationPagedQuery, PagedFormationResponse> {

    private final FormationRepository formationRepository;

    public GetFormationPagedHandler(FormationRepository formationRepository) {
        this.formationRepository = formationRepository;
    }

    @Override
    public PagedFormationResponse handle(GetFormationPagedQuery query) {
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
        
        // Fetch paginated formations with lightweight columns
        Page<Formation> formations = formationRepository.findAll(pageable);
        
        // Convert to response DTOs
        var content = formations.getContent().stream()
            .map(this::convertToResponse)
            .toList();
        
        // Build paged response
        return new PagedFormationResponse(
            content,
            formations.getNumber(),
            formations.getSize(),
            formations.getTotalElements(),
            formations.getTotalPages(),
            formations.hasNext(),
            formations.hasPrevious()
        );
    }
    
    private FormationSummaryDTO convertToResponse(Formation formation) {
        FormationSummaryDTO response = new FormationSummaryDTO();
        response.setIdFormation(formation.getIdFormation());
        response.setTheme(formation.getTheme());
        response.setDescriptionTheme(formation.getDescriptionTheme());
        response.setNombreHeures(formation.getNombreHeures());
        response.setPrix(formation.getPrix());
        response.setNombreMax(formation.getNombreMax());
        response.setPopulationCible(formation.getPopulationCible());
        response.setTypeFormation(formation.getTypeFormation());
        response.setNiveau(formation.getNiveau());
        response.setCategorie(formation.getCategorie() != null ? formation.getCategorie().getNom() : null);
        response.setStatut(formation.getStatut());
        return response;
    }
}
