package com.example.BacK.application.g_Utilisateur.Command.role.removePermission;

import com.example.BacK.application.interfaces.g_Utilisateur.role.IRoleRepositoryService;
import com.example.BacK.application.mediator.RequestHandler;
import org.springframework.stereotype.Component;

@Component("RemovePermissionHandler")
public class RemovePermissionHandler implements RequestHandler<RemovePermissionCommand, RemovePermissionResponse> {

    private final IRoleRepositoryService roleRepositoryService;

    public RemovePermissionHandler(IRoleRepositoryService roleRepositoryService) {
        this.roleRepositoryService = roleRepositoryService;
    }

    @Override
    public RemovePermissionResponse handle(RemovePermissionCommand command) {
        roleRepositoryService.removePermission(command.getRoleId(), command.getPermissionId());
        return new RemovePermissionResponse();
    }
}
