package com.example.BacK.application.g_RH.Command.congee.addCongee;

import com.example.BacK.application.interfaces.g_rh.congee.IAddCongeeCommand;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AddCongeeValidator implements ConstraintValidator<IAddCongeeCommand, AddCongeeCommand> {
    @Override
    public boolean isValid(AddCongeeCommand command, ConstraintValidatorContext constraintValidatorContext) {
        if (command == null) {
            return false;
        }
        return true;
    }
}
