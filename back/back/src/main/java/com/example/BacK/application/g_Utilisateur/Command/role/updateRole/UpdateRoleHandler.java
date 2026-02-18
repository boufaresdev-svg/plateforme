package com.example.BacK.application.g_Utilisateur.Command.role.updateRole;

import com.example.BacK.application.interfaces.g_Utilisateur.role.IRoleRepositoryService;
import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Utilisateurs.Role;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component("UpdateRoleHandler")
public class UpdateRoleHandler implements RequestHandler<UpdateRoleCommand, UpdateRoleResponse> {

    private final IRoleRepositoryService roleRepositoryService;
    private final ModelMapper modelMapper;

    public UpdateRoleHandler(IRoleRepositoryService roleRepositoryService, ModelMapper modelMapper) {
        this.roleRepositoryService = roleRepositoryService;
        this.modelMapper = modelMapper;
    }

    @Override
    public UpdateRoleResponse handle(UpdateRoleCommand command) {
        Role role = modelMapper.map(command, Role.class);
        // Preserve the systemRole flag - it should not be changed via updates
        roleRepositoryService.update(role, command.getPermissionIds());
        return new UpdateRoleResponse(command.getId());
    }
}
