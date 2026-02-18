package com.example.BacK.application.g_Utilisateur.Command.role.assignPermission;

import com.example.BacK.application.interfaces.g_Utilisateur.role.IRoleRepositoryService;
import com.example.BacK.application.mediator.RequestHandler;
import org.springframework.stereotype.Component;

@Component("AssignPermissionHandler")
public class AssignPermissionHandler implements RequestHandler<AssignPermissionCommand, AssignPermissionResponse> {

    private final IRoleRepositoryService roleRepositoryService;

    public AssignPermissionHandler(IRoleRepositoryService roleRepositoryService) {
        this.roleRepositoryService = roleRepositoryService;
    }

    @Override
    public AssignPermissionResponse handle(AssignPermissionCommand command) {
        roleRepositoryService.assignPermission(command.getRoleId(), command.getPermissionId());
        return new AssignPermissionResponse();
    }
}
