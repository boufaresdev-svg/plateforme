package com.example.BacK.application.g_Utilisateur.Command.user.updateUser;

import com.example.BacK.application.interfaces.g_Utilisateur.user.IUserRepositoryService;
import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Utilisateurs.User;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component("UpdateUserHandler")
public class UpdateUserHandler implements RequestHandler<UpdateUserCommand, UpdateUserResponse> {

    private final IUserRepositoryService userRepositoryService;
    private final ModelMapper modelMapper;

    public UpdateUserHandler(IUserRepositoryService userRepositoryService, ModelMapper modelMapper) {
        this.userRepositoryService = userRepositoryService;
        this.modelMapper = modelMapper;
    }

    @Override
    public UpdateUserResponse handle(UpdateUserCommand command) {
        User user = modelMapper.map(command, User.class);
        userRepositoryService.update(user, command.getRoleIds(), command.getMotDePasse());
        return new UpdateUserResponse(command.getId());
    }
}
