package com.example.BacK.application.g_Utilisateur.Command.user.updateUser;

import org.springframework.stereotype.Component;

import com.example.BacK.infrastructure.repository.g_Utilisateur.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Component
public class UpdateUserValidator {
    
    private final UserRepository userRepository;
    
    public UpdateUserValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    public List<String> validate(UpdateUserCommand command) {
        List<String> errors = new ArrayList<>();
        
        if (!userRepository.existsById(command.getId())) {
            errors.add("L'utilisateur avec l'ID '" + command.getId() + "' n'existe pas");
        }
        
        // Check if email is already used by another user
        userRepository.findByEmail(command.getEmail()).ifPresent(existingUser -> {
            if (!existingUser.getId().equals(command.getId())) {
                errors.add("L'email '" + command.getEmail() + "' est déjà utilisé par un autre utilisateur");
            }
        });
        
        return errors;
    }
}
