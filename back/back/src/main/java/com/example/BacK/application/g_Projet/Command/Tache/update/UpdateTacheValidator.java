package com.example.BacK.application.g_Projet.Command.Tache.update;

import com.example.BacK.application.interfaces.g_Projet.tache.IUpdateTacheCommand;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


public class UpdateTacheValidator implements ConstraintValidator<IUpdateTacheCommand, UpdateTacheCommand> {
    @Override
    public boolean isValid(UpdateTacheCommand command, ConstraintValidatorContext constraintValidatorContext) {
        if (command == null) {
            return false;
        }
        return true;
    }
}