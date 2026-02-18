package com.example.BacK.application.g_Vehicule.Command.reparation.updateReparation;

import com.example.BacK.application.interfaces.g_Vehicule.Reparation.IUpdateReparationCommand;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UpdateReparationValidator implements ConstraintValidator<IUpdateReparationCommand, UpdateReparationCommand> {
    @Override
    public boolean isValid(UpdateReparationCommand command, ConstraintValidatorContext constraintValidatorContext) {
        if (command == null) {
            return false;
        }
        return true;
    }
}