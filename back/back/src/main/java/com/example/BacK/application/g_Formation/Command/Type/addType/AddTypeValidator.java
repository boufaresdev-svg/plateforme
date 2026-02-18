package com.example.BacK.application.g_Formation.Command.Type.addType;

import com.example.BacK.application.interfaces.g_Formation.Type.IAddTypeCommand;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AddTypeValidator implements ConstraintValidator<IAddTypeCommand, AddTypeCommand> {
    @Override
    public boolean isValid(AddTypeCommand addTypeCommand, ConstraintValidatorContext constraintValidatorContext) {
        return false;
    }
}
