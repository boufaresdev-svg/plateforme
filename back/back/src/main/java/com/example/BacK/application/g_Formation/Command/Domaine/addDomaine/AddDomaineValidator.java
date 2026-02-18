package com.example.BacK.application.g_Formation.Command.Domaine.addDomaine;

import com.example.BacK.application.interfaces.g_Formation.Domaine.IAddDomaineCommand;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AddDomaineValidator implements ConstraintValidator<IAddDomaineCommand, AddDomaineCommand> {
    @Override
    public boolean isValid(AddDomaineCommand addDomaineCommand, ConstraintValidatorContext constraintValidatorContext) {
        return false;
    }
}
