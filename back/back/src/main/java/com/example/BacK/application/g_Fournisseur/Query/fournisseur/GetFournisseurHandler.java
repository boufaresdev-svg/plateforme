package com.example.BacK.application.g_Fournisseur.Query.fournisseur;

import com.example.BacK.application.interfaces.g_Fournisseur.fournisseur.IFournisseurRepositoryService;
import com.example.BacK.application.mediator.RequestHandler;
import org.springframework.stereotype.Component;

@Component("GetFournisseurHandler")
public class GetFournisseurHandler implements RequestHandler<GetFournisseurQuery, Object> {

    private final IFournisseurRepositoryService fournisseurRepositoryService;

    public GetFournisseurHandler(IFournisseurRepositoryService fournisseurRepositoryService) {
        this.fournisseurRepositoryService = fournisseurRepositoryService;
    }

    @Override
    public Object handle(GetFournisseurQuery query) {
        // If requesting a specific fournisseur by ID, return without pagination
        if (query.getId() != null && !query.getId().trim().isEmpty()) {
            return fournisseurRepositoryService.getById(query);
        }
        
        // Check if pagination is requested (page or size provided)
        boolean isPaginated = query.getPage() != null || query.getSize() != null;
        
        if (hasSearchCriteria(query)) {
            if (isPaginated) {
                return fournisseurRepositoryService.searchPaginated(query);
            }
            return fournisseurRepositoryService.search(query);
        }
        
        if (isPaginated) {
            return fournisseurRepositoryService.getAllPaginated(query);
        }
        
        return fournisseurRepositoryService.getAll();
    }
    
    private boolean hasSearchCriteria(GetFournisseurQuery query) {
        return (query.getNom() != null && !query.getNom().trim().isEmpty()) ||
               (query.getInfoContact() != null && !query.getInfoContact().trim().isEmpty()) ||
               (query.getAdresse() != null && !query.getAdresse().trim().isEmpty()) ||
               (query.getTelephone() != null && !query.getTelephone().trim().isEmpty()) ||
               (query.getMatriculeFiscale() != null && !query.getMatriculeFiscale().trim().isEmpty());
    }
}