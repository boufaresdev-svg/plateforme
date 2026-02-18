package com.example.BacK.application.g_Utilisateur.Query.role;

import com.example.BacK.application.interfaces.g_Utilisateur.role.IRoleRepositoryService;
import com.example.BacK.application.mediator.RequestHandler;
import org.springframework.stereotype.Component;

@Component("GetRoleHandler")
public class GetRoleHandler implements RequestHandler<GetRoleQuery, Object> {

    private final IRoleRepositoryService roleRepositoryService;

    public GetRoleHandler(IRoleRepositoryService roleRepositoryService) {
        this.roleRepositoryService = roleRepositoryService;
    }
    @Override
    public Object handle(GetRoleQuery query) {
        if (query.getId() != null && !query.getId().trim().isEmpty()) {
            return roleRepositoryService.getById(query);
        }
        return roleRepositoryService.getAll();
    }
}
