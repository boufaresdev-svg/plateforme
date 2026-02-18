package com.example.BacK.application.g_Utilisateur.Command.user.addUser;

import com.example.BacK.application.interfaces.g_Utilisateur.user.IUserRepositoryService;
import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Utilisateurs.User;
import com.example.BacK.domain.g_Utilisateurs.UserStatus;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component("AddUserHandler")
public class AddUserHandler implements RequestHandler<AddUserCommand, AddUserResponse> {

    private final IUserRepositoryService userRepositoryService;
    private final ModelMapper modelMapper;

    public AddUserHandler(IUserRepositoryService userRepositoryService, ModelMapper modelMapper) {
        this.userRepositoryService = userRepositoryService;
        this.modelMapper = modelMapper;
    }

    @Override
    public AddUserResponse handle(AddUserCommand command) {
        User user = modelMapper.map(command, User.class);
        // Set default status if not provided
        if (user.getStatut() == null) {
            user.setStatut(UserStatus.EN_ATTENTE);
        }
        String id = userRepositoryService.add(user, command.getRoleIds());
        return new AddUserResponse(id);
    }
}
