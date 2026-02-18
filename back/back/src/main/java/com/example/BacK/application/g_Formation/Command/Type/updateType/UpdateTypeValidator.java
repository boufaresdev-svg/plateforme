package com.example.BacK.application.g_Formation.Command.Type.updateType;

import com.example.BacK.application.interfaces.g_Formation.Type.IUpdateTypeCommand;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UpdateTypeValidator implements ConstraintValidator<IUpdateTypeCommand, UpdateTypeCommand> {
    @Override
    public boolean isValid(UpdateTypeCommand updateTypeCommand, ConstraintValidatorContext constraintValidatorContext) {
        return false;
    }
}
