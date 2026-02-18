package com.example.BacK.application.g_RH.Command.retenue.add;

import com.example.BacK.application.interfaces.g_rh.retenue.IAddRetenueCommand;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AddRetenueValidator implements ConstraintValidator<IAddRetenueCommand, AddRetenueCommand> {
    @Override
    public boolean isValid(AddRetenueCommand command, ConstraintValidatorContext constraintValidatorContext) {
        if (command == null) {
            return false;
        }
        return true;
    }
}