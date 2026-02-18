package com.example.BacK.application.g_Formation.Command.Formation.addFormation;

import com.example.BacK.application.interfaces.g_Formation.Formation.IAddFormationCommand;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AddFormationValidator implements ConstraintValidator<IAddFormationCommand, AddFormationCommand> {


    @Override
    public boolean isValid(AddFormationCommand addFormationCommand, ConstraintValidatorContext constraintValidatorContext) {
        return false;
    }
}