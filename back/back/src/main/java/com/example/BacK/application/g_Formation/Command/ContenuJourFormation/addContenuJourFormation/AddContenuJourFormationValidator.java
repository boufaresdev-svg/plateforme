package com.example.BacK.application.g_Formation.Command.ContenuJourFormation.addContenuJourFormation;

import com.example.BacK.application.interfaces.g_Formation.ContenuJourFormation.IAddContenuJourFormationCommand;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AddContenuJourFormationValidator implements ConstraintValidator<IAddContenuJourFormationCommand, AddContenuJourFormationCommand> {
    @Override
    public boolean isValid(AddContenuJourFormationCommand addContenuJourFormationCommand, ConstraintValidatorContext constraintValidatorContext) {
        return false;
    }
}
