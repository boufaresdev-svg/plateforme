package com.example.BacK.application.g_Projet.Command.phase.update;

import com.example.BacK.application.interfaces.g_Projet.phase.IUpdatePhaseCommand;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


public class UpdatePhaseValidator implements ConstraintValidator<IUpdatePhaseCommand, UpdatePhaseCommand> {
    @Override
    public boolean isValid(UpdatePhaseCommand command, ConstraintValidatorContext constraintValidatorContext) {
        if (command == null) {
            return false;
        }
        return true;
    }
}