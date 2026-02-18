package com.example.BacK.application.g_Formation.Command.ContenuGlobal.updateContenuGlobal;

import com.example.BacK.application.interfaces.g_Formation.ContenuGlobal.IUpdateContenuGlobalCommand;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UpdateContenuGlobalValidator implements ConstraintValidator<IUpdateContenuGlobalCommand, UpdateContenuGlobalCommand> {
    @Override
    public boolean isValid(UpdateContenuGlobalCommand updateContenuGlobalCommand, ConstraintValidatorContext constraintValidatorContext) {
        return false;
    }
}
