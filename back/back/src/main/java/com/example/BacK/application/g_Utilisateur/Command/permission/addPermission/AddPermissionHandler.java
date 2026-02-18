package com.example.BacK.application.g_Utilisateur.Command.permission.addPermission;

import com.example.BacK.application.interfaces.g_Utilisateur.permission.IPermissionRepositoryService;
import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Utilisateurs.Permission;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component("AddPermissionHandler")
public class AddPermissionHandler implements RequestHandler<AddPermissionCommand, AddPermissionResponse> {

    private final IPermissionRepositoryService permissionRepositoryService;
    private final ModelMapper modelMapper;

    public AddPermissionHandler(IPermissionRepositoryService permissionRepositoryService, ModelMapper modelMapper) {
        this.permissionRepositoryService = permissionRepositoryService;
        this.modelMapper = modelMapper;
    }

    @Override
    public AddPermissionResponse handle(AddPermissionCommand command) {
        Permission permission = modelMapper.map(command, Permission.class);
        String id = permissionRepositoryService.add(permission);
        return new AddPermissionResponse(id);
    }
}
