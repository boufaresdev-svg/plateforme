package com.example.BacK.application.g_Utilisateur.Command.permission.updatePermission;

import com.example.BacK.application.interfaces.g_Utilisateur.permission.IPermissionRepositoryService;
import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Utilisateurs.Permission;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component("UpdatePermissionHandler")
public class UpdatePermissionHandler implements RequestHandler<UpdatePermissionCommand, UpdatePermissionResponse> {

    private final IPermissionRepositoryService permissionRepositoryService;
    private final ModelMapper modelMapper;

    public UpdatePermissionHandler(IPermissionRepositoryService permissionRepositoryService, ModelMapper modelMapper) {
        this.permissionRepositoryService = permissionRepositoryService;
        this.modelMapper = modelMapper;
    }

    @Override
    public UpdatePermissionResponse handle(UpdatePermissionCommand command) {
        Permission permission = modelMapper.map(command, Permission.class);
        permissionRepositoryService.update(permission);
        return new UpdatePermissionResponse(command.getId());
    }
}
