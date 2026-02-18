package com.example.BacK.application.g_Projet.Command.charge.update;

import com.example.BacK.application.interfaces.g_Projet.charge.IUpdateChargeCommand;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UpdateChargeValidator implements ConstraintValidator<IUpdateChargeCommand, UpdateChargeCommand> {
    @Override
    public boolean isValid(UpdateChargeCommand command, ConstraintValidatorContext constraintValidatorContext) {
        if (command == null) {
            return false;
        }
        return true;
    }
}