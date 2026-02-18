package com.example.BacK.application.g_Projet.Command.phase.add;

import com.example.BacK.application.interfaces.g_Projet.phase.IAddPhaseCommand;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


public class AddPhaseValidator implements ConstraintValidator<IAddPhaseCommand, AddPhaseCommand> {
    @Override
    public boolean isValid(AddPhaseCommand command, ConstraintValidatorContext constraintValidatorContext) {
        if (command == null) {
            return false;
        }
        return true;
    }
}