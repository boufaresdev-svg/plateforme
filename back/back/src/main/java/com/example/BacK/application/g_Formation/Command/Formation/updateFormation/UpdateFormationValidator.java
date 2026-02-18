package com.example.BacK.application.g_Formation.Command.Formation.updateFormation;

import com.example.BacK.application.interfaces.g_Formation.Formation.IUpdateFormationCommand;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UpdateFormationValidator implements ConstraintValidator<IUpdateFormationCommand, UpdateFormationCommand> {
    @Override
    public boolean isValid(UpdateFormationCommand updateFormationCommand, ConstraintValidatorContext constraintValidatorContext) {
        if (updateFormationCommand == null) {
            return false;
        }
        return true;
    }
}
