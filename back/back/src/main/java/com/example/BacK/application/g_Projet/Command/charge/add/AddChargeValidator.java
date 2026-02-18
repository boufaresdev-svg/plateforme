package com.example.BacK.application.g_Projet.Command.charge.add;

import com.example.BacK.application.interfaces.g_Projet.charge.IAddChargeCommand;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AddChargeValidator implements ConstraintValidator<IAddChargeCommand, AddChargeCommand> {
    @Override
    public boolean isValid(AddChargeCommand command, ConstraintValidatorContext constraintValidatorContext) {
        if (command == null) {
            return false;
        }
        return true;
    }
}