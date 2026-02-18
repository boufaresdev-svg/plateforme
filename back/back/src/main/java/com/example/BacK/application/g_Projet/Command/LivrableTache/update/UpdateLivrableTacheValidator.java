package com.example.BacK.application.g_Projet.Command.LivrableTache.update;

import com.example.BacK.application.interfaces.g_Projet.LivrableTache.IUpdateLivrableTacheCommand;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


public class UpdateLivrableTacheValidator implements ConstraintValidator<IUpdateLivrableTacheCommand, UpdateLivrableTacheCommand> {
    @Override
    public boolean isValid(UpdateLivrableTacheCommand command, ConstraintValidatorContext constraintValidatorContext) {
        if (command == null) {
            return false;
        }
        return true;
    }
}