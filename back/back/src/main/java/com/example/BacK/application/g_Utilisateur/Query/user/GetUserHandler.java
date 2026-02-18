package com.example.BacK.application.g_Utilisateur.Query.user;

import com.example.BacK.application.interfaces.g_Utilisateur.user.IUserRepositoryService;
import com.example.BacK.application.mediator.RequestHandler;
import org.springframework.stereotype.Component;

@Component("GetUserHandler")
public class GetUserHandler implements RequestHandler<GetUserQuery, Object> {

    private final IUserRepositoryService userRepositoryService;

    public GetUserHandler(IUserRepositoryService userRepositoryService) {
        this.userRepositoryService = userRepositoryService;
    }

    @Override
    public Object handle(GetUserQuery query) {
        if (query.getId() != null && !query.getId().trim().isEmpty()) {
            return userRepositoryService.getById(query);
        }
        
        // Check if pagination is requested (page or size provided)
        boolean isPaginated = query.getPage() != null || query.getSize() != null;
        
        if (hasSearchCriteria(query)) {
            if (isPaginated) {
                return userRepositoryService.searchPaginated(query);
            }
            return userRepositoryService.search(query);
        }
        
        if (isPaginated) {
            return userRepositoryService.getAllPaginated(query);
        }
        
        return userRepositoryService.getAll();
    }
    
    private boolean hasSearchCriteria(GetUserQuery query) {
        return (query.getNomUtilisateur() != null && !query.getNomUtilisateur().trim().isEmpty()) ||
               (query.getEmail() != null && !query.getEmail().trim().isEmpty()) ||
               (query.getPrenom() != null && !query.getPrenom().trim().isEmpty()) ||
               (query.getNom() != null && !query.getNom().trim().isEmpty()) ||
               (query.getNumeroTelephone() != null && !query.getNumeroTelephone().trim().isEmpty()) ||
               (query.getDepartement() != null && !query.getDepartement().trim().isEmpty()) ||
               (query.getPoste() != null && !query.getPoste().trim().isEmpty()) ||
               (query.getSearch() != null && !query.getSearch().trim().isEmpty()) ||
               query.getStatut() != null;
    }
}
