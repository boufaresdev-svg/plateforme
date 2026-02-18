package com.example.BacK.application.g_Utilisateur.Command.user.deleteUser;

import org.springframework.stereotype.Component;

import com.example.BacK.infrastructure.repository.g_Utilisateur.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Component
public class DeleteUserValidator {
    
    private final UserRepository userRepository;
    
    public DeleteUserValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    public List<String> validate(DeleteUserCommand command) {
        List<String> errors = new ArrayList<>();
        
        if (!userRepository.existsById(command.getId())) {
            errors.add("L'utilisateur avec l'ID '" + command.getId() + "' n'existe pas");
        }
        
        return errors;
    }
}
