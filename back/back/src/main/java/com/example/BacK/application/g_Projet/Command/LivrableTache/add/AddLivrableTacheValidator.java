package com.example.BacK.application.g_Projet.Command.LivrableTache.add;

import com.example.BacK.application.interfaces.g_Projet.LivrableTache.IAddLivrableTacheCommand;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


public class AddLivrableTacheValidator implements ConstraintValidator<IAddLivrableTacheCommand, AddLivrableTacheCommand> {
    @Override
    public boolean isValid(AddLivrableTacheCommand command, ConstraintValidatorContext constraintValidatorContext) {
        if (command == null) {
            return false;
        }
        return true;
    }
}