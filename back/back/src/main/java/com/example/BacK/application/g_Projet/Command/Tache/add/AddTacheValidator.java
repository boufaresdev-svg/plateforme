package com.example.BacK.application.g_Projet.Command.Tache.add;

import com.example.BacK.application.interfaces.g_Projet.tache.IAddTacheCommand;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AddTacheValidator implements ConstraintValidator<IAddTacheCommand, AddTacheCommand> {
    @Override
    public boolean isValid(AddTacheCommand command, ConstraintValidatorContext constraintValidatorContext) {
        if (command == null) {
            return false;
        }
        return true;
    }
}