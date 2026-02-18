package com.example.BacK.application.g_Formation.Command.Apprenant.addApprenant;

import com.example.BacK.application.interfaces.g_Formation.Apprenant.IAddApprenantCommand;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AddApprenantValidator implements ConstraintValidator<IAddApprenantCommand, AddApprenantCommand> {
    @Override
    public boolean isValid(AddApprenantCommand addApprenantCommand, ConstraintValidatorContext constraintValidatorContext) {
        return false;
    }
}
