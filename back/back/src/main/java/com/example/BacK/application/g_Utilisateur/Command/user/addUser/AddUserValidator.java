package com.example.BacK.application.g_Utilisateur.Command.user.addUser;

import org.springframework.stereotype.Component;

import com.example.BacK.infrastructure.repository.g_Utilisateur.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Component
public class AddUserValidator {
    
    private final UserRepository userRepository;
    
    public AddUserValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    public List<String> validate(AddUserCommand command) {
        List<String> errors = new ArrayList<>();
        
        if (userRepository.existsByNomUtilisateur(command.getNomUtilisateur())) {
            errors.add("Le nom d'utilisateur '" + command.getNomUtilisateur() + "' existe déjà");
        }
        
        if (userRepository.existsByEmail(command.getEmail())) {
            errors.add("L'email '" + command.getEmail() + "' existe déjà");
        }
        
        return errors;
    }
}
