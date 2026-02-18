package com.example.BacK.application.g_Projet.Command.projet.update;

import com.example.BacK.application.g_Projet.Command.commentaireTache.add.AddCommentaireTacheCommand;

import com.example.BacK.application.interfaces.g_Projet.projet.IUpdateProjetCommand;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


public class UpdateProjetValidator implements ConstraintValidator<IUpdateProjetCommand, UpdateProjetCommand> {
    @Override
    public boolean isValid(UpdateProjetCommand command, ConstraintValidatorContext constraintValidatorContext) {
        if (command == null) {
            return false;
        }
        return true;
    }
}