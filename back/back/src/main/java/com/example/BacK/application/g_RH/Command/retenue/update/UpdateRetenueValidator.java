package com.example.BacK.application.g_RH.Command.retenue.update;

import com.example.BacK.application.interfaces.g_rh.retenue.IUpdateRetenueCommand;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


public class UpdateRetenueValidator implements ConstraintValidator<IUpdateRetenueCommand, UpdateRetenueCommand> {
    @Override
    public boolean isValid(UpdateRetenueCommand command, ConstraintValidatorContext constraintValidatorContext) {
        if (command == null) {
            return false;
        }
        return true;
    }
}