package com.example.BacK.application.g_Utilisateur.Command.user.removeRole;

import com.example.BacK.application.interfaces.g_Utilisateur.user.IUserRepositoryService;
import com.example.BacK.application.mediator.RequestHandler;
import org.springframework.stereotype.Component;

@Component("RemoveRoleHandler")
public class RemoveRoleHandler implements RequestHandler<RemoveRoleCommand, RemoveRoleResponse> {

    private final IUserRepositoryService userRepositoryService;

    public RemoveRoleHandler(IUserRepositoryService userRepositoryService) {
        this.userRepositoryService = userRepositoryService;
    }

    @Override
    public RemoveRoleResponse handle(RemoveRoleCommand command) {
        userRepositoryService.removeRole(command.getUserId(), command.getRoleId());
        return new RemoveRoleResponse();
    }
}
