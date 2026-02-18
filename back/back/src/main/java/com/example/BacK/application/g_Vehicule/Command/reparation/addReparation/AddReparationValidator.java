package com.example.BacK.application.g_Vehicule.Command.reparation.addReparation;

import com.example.BacK.application.interfaces.g_Vehicule.Reparation.IAddReparationCommand;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


public class AddReparationValidator implements ConstraintValidator<IAddReparationCommand, AddReparationCommand> {
    @Override
    public boolean isValid(AddReparationCommand command, ConstraintValidatorContext constraintValidatorContext) {
        if (command == null) {
            return false;
        }
        return true;
    }
}