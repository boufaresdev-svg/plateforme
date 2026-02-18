package com.example.BacK.application.g_Fournisseur.Query.dettesFournisseur;

import com.example.BacK.application.interfaces.g_Fournisseur.dettesFournisseur.IDettesFournisseurRepositoryService;
import com.example.BacK.application.mediator.RequestHandler;
import org.springframework.stereotype.Component;

@Component("GetDettesFournisseurHandler")
public class GetDettesFournisseurHandler implements RequestHandler<GetDettesFournisseurQuery, Object> {

    private final IDettesFournisseurRepositoryService dettesFournisseurRepositoryService;

    public GetDettesFournisseurHandler(IDettesFournisseurRepositoryService dettesFournisseurRepositoryService) {
        this.dettesFournisseurRepositoryService = dettesFournisseurRepositoryService;
    }

    @Override
    public Object handle(GetDettesFournisseurQuery query) {
        // Check if pagination is requested (page or size provided)
        boolean isPaginated = query.getPage() != null || query.getSize() != null;
        
        // Check if search criteria are provided
        if (hasSearchCriteria(query)) {
            if (isPaginated) {
                return dettesFournisseurRepositoryService.searchPaginated(query);
            }
            return dettesFournisseurRepositoryService.search(query);
        }
        
        // If no search criteria, return all results
        if (isPaginated) {
            return dettesFournisseurRepositoryService.getAllPaginated(query);
        }
        
        return dettesFournisseurRepositoryService.getAll();
    }
    
    private boolean hasSearchCriteria(GetDettesFournisseurQuery query) {
        return (query.getNumeroFacture() != null && !query.getNumeroFacture().trim().isEmpty()) ||
               (query.getTitre() != null && !query.getTitre().trim().isEmpty()) ||
               query.getEstPaye() != null ||
               query.getDatePaiementPrevue() != null ||
               query.getDatePaiementReelle() != null ||
               (query.getFournisseurId() != null && !query.getFournisseurId().isEmpty()) ||
               (query.getFournisseurNom() != null && !query.getFournisseurNom().trim().isEmpty());
    }
}