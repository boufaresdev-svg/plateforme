package com.example.BacK.application.g_Utilisateur.Query.permission;

import com.example.BacK.application.interfaces.g_Utilisateur.permission.IPermissionRepositoryService;
import com.example.BacK.application.mediator.RequestHandler;
import org.springframework.stereotype.Component;

@Component("GetPermissionHandler")
public class GetPermissionHandler implements RequestHandler<GetPermissionQuery, Object> {

    private final IPermissionRepositoryService permissionRepositoryService;

    public GetPermissionHandler(IPermissionRepositoryService permissionRepositoryService) {
        this.permissionRepositoryService = permissionRepositoryService;
    }

    @Override
    public Object handle(GetPermissionQuery query) {
        if (query.getId() != null && !query.getId().trim().isEmpty()) {
            return permissionRepositoryService.getById(query);
        }
        
        return permissionRepositoryService.getAll();
    }
}
