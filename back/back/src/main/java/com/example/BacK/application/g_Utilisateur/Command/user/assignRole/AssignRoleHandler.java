package com.example.BacK.application.g_Utilisateur.Command.user.assignRole;

import com.example.BacK.application.interfaces.g_Utilisateur.user.IUserRepositoryService;
import com.example.BacK.application.mediator.RequestHandler;
import org.springframework.stereotype.Component;

@Component("AssignRoleHandler")
public class AssignRoleHandler implements RequestHandler<AssignRoleCommand, AssignRoleResponse> {

    private final IUserRepositoryService userRepositoryService;

    public AssignRoleHandler(IUserRepositoryService userRepositoryService) {
        this.userRepositoryService = userRepositoryService;
    }

    @Override
    public AssignRoleResponse handle(AssignRoleCommand command) {
        userRepositoryService.assignRole(command.getUserId(), command.getRoleId());
        return new AssignRoleResponse();
    }
}
