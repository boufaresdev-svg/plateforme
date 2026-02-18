package com.example.BacK.application.g_Utilisateur.Command.permission.deletePermission;

import com.example.BacK.application.interfaces.g_Utilisateur.permission.IPermissionRepositoryService;
import com.example.BacK.application.mediator.RequestHandler;
import org.springframework.stereotype.Component;

@Component("DeletePermissionHandler")
public class DeletePermissionHandler implements RequestHandler<DeletePermissionCommand, DeletePermissionResponse> {

    private final IPermissionRepositoryService permissionRepositoryService;

    public DeletePermissionHandler(IPermissionRepositoryService permissionRepositoryService) {
        this.permissionRepositoryService = permissionRepositoryService;
    }

    @Override
    public DeletePermissionResponse handle(DeletePermissionCommand command) {
        permissionRepositoryService.delete(command.getId());
        return new DeletePermissionResponse();
    }
}
