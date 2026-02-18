package com.example.BacK.application.g_Utilisateur.Command.user.deleteUser;

import com.example.BacK.application.interfaces.g_Utilisateur.user.IUserRepositoryService;
import com.example.BacK.application.mediator.RequestHandler;
import org.springframework.stereotype.Component;

@Component("DeleteUserHandler")
public class DeleteUserHandler implements RequestHandler<DeleteUserCommand, DeleteUserResponse> {

    private final IUserRepositoryService userRepositoryService;

    public DeleteUserHandler(IUserRepositoryService userRepositoryService) {
        this.userRepositoryService = userRepositoryService;
    }

    @Override
    public DeleteUserResponse handle(DeleteUserCommand command) {
        userRepositoryService.delete(command.getId());
        return new DeleteUserResponse();
    }
}
