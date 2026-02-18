package com.example.BacK.application.g_RH.Command.regle.update;

import com.example.BacK.application.interfaces.g_rh.regle.IUpdateRegleCommand;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


public class UpdateRegleValidator implements ConstraintValidator<IUpdateRegleCommand, UpdateRegleCommand> {
    @Override
    public boolean isValid(UpdateRegleCommand command, ConstraintValidatorContext constraintValidatorContext) {
        if (command == null) {
            return false;
        }
        return true;
    }
}
