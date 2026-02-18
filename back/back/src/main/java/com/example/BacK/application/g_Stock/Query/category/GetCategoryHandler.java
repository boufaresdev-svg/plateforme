package com.example.BacK.application.g_Stock.Query.category;

import com.example.BacK.application.interfaces.g_Stock.category.ICategoryRepositoryService;
import com.example.BacK.application.mediator.RequestHandler;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("GetCategoryHandler")
public class GetCategoryHandler implements RequestHandler<GetCategoryQuery, List<GetCategoryResponse>> {

    private final ICategoryRepositoryService categoryRepositoryService;

    public GetCategoryHandler(ICategoryRepositoryService categoryRepositoryService) {
        this.categoryRepositoryService = categoryRepositoryService;
    }

    @Override
    public List<GetCategoryResponse> handle(GetCategoryQuery query) {
        if (query.getId() != null && !query.getId().isEmpty()) {
            return categoryRepositoryService.getById(query);
        }
        
        if (hasSearchCriteria(query)) {
            return categoryRepositoryService.search(query);
        }
        
        return categoryRepositoryService.getAll();
    }
    
    private boolean hasSearchCriteria(GetCategoryQuery query) {
        return (query.getNom() != null && !query.getNom().isEmpty()) ||
               (query.getDescription() != null && !query.getDescription().isEmpty()) ||
               query.getEstActif() != null;
    }
}