package com.example.BacK.application.g_Formation.Command.ContenuJourFormation.updateContenuJourFormation;

import com.example.BacK.application.interfaces.g_Formation.ContenuJourFormation.IUpdateContenuJourFormationCommand;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UpdateContenuJourFormationValidator implements ConstraintValidator<IUpdateContenuJourFormationCommand , UpdateContenuJourFormationCommand> {
    @Override
    public boolean isValid(UpdateContenuJourFormationCommand updateContenuJourFormationCommand, ConstraintValidatorContext constraintValidatorContext) {
        return false;
    }
}
