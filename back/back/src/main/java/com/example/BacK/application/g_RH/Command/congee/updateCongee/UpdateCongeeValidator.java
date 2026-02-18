package com.example.BacK.application.g_RH.Command.congee.updateCongee;


import com.example.BacK.application.interfaces.g_rh.congee.IUpdateCongeeCommand;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


public class UpdateCongeeValidator implements ConstraintValidator<IUpdateCongeeCommand, UpdateCongeeCommand> {
    @Override
    public boolean isValid(UpdateCongeeCommand command, ConstraintValidatorContext constraintValidatorContext) {
        if (command == null) {
            return false;
        }
        if (command.getEmployee () == null ) {
            throw new IllegalArgumentException("Le Employee est obligatoire");
        }

        return true;
    }
}