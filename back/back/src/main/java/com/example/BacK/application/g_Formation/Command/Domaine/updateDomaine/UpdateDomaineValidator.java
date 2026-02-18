package com.example.BacK.application.g_Formation.Command.Domaine.updateDomaine;

import com.example.BacK.application.interfaces.g_Formation.Domaine.IUpdateDomaineCommand;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UpdateDomaineValidator implements ConstraintValidator<IUpdateDomaineCommand, UpdateDomaineCommand> {
    @Override
    public boolean isValid(UpdateDomaineCommand updateDomaineCommand, ConstraintValidatorContext constraintValidatorContext) {
        return false;
    }
}
