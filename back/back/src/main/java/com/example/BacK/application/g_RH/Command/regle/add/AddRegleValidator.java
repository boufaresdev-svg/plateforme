package com.example.BacK.application.g_RH.Command.regle.add;

import com.example.BacK.application.interfaces.g_rh.regle.IAddRegleCommand;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AddRegleValidator implements ConstraintValidator<IAddRegleCommand, AddRegleCommand> {
    @Override
    public boolean isValid(AddRegleCommand command, ConstraintValidatorContext constraintValidatorContext) {
        if (command == null) {
            return false;
        }
        return true;
    }
}
