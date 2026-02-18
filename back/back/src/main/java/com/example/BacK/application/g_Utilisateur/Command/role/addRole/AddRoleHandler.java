package com.example.BacK.application.g_Utilisateur.Command.role.addRole;

import com.example.BacK.application.interfaces.g_Utilisateur.role.IRoleRepositoryService;
import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Utilisateurs.Role;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component("AddRoleHandler")
public class AddRoleHandler implements RequestHandler<AddRoleCommand, AddRoleResponse> {

    private final IRoleRepositoryService roleRepositoryService;
    private final ModelMapper modelMapper;

    public AddRoleHandler(IRoleRepositoryService roleRepositoryService, ModelMapper modelMapper) {
        this.roleRepositoryService = roleRepositoryService;
        this.modelMapper = modelMapper;
    }

    @Override
    public AddRoleResponse handle(AddRoleCommand command) {
        Role role = modelMapper.map(command, Role.class);
        // Ensure systemRole is set to false for manually created roles
        role.setSystemRole(false);
        String id = roleRepositoryService.add(role, command.getPermissionIds());
        return new AddRoleResponse(id);
    }
}
