package com.example.BacK.application.g_Utilisateur.Query.permission;

import com.example.BacK.application.interfaces.g_Utilisateur.permission.IPermissionRepositoryService;
import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Utilisateurs.Module;

import org.springframework.stereotype.Component;

@Component("GetPermissionsByModuleHandler")
public class GetPermissionsByModuleHandler implements RequestHandler<GetPermissionsByModuleQuery, Object> {

    private final IPermissionRepositoryService permissionRepositoryService;

    public GetPermissionsByModuleHandler(IPermissionRepositoryService permissionRepositoryService) {
        this.permissionRepositoryService = permissionRepositoryService;
    }

    @Override
    public Object handle(GetPermissionsByModuleQuery query) {
        try {
            // Validate module code
            Module.valueOf(query.getModuleCode());
            return permissionRepositoryService.getAll();
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Module invalide: " + query.getModuleCode(), e);
        }
    }
}
