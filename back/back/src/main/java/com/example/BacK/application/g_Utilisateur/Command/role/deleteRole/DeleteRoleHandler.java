package com.example.BacK.application.g_Utilisateur.Command.role.deleteRole;

import com.example.BacK.application.interfaces.g_Utilisateur.role.IRoleRepositoryService;
import com.example.BacK.application.mediator.RequestHandler;
import org.springframework.stereotype.Component;

@Component("DeleteRoleHandler")
public class DeleteRoleHandler implements RequestHandler<DeleteRoleCommand, DeleteRoleResponse> {

    private final IRoleRepositoryService roleRepositoryService;

    public DeleteRoleHandler(IRoleRepositoryService roleRepositoryService) {
        this.roleRepositoryService = roleRepositoryService;
    }

    @Override
    public DeleteRoleResponse handle(DeleteRoleCommand command) {
        roleRepositoryService.delete(command.getId());
        return new DeleteRoleResponse();
    }
}
