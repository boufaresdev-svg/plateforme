package com.example.BacK.application.g_Formation.Command.ContenuGlobal.addContenuGlobal;

import com.example.BacK.application.interfaces.g_Formation.ContenuGlobal.IAddContenuGlobalCommand;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AddContenuGlobalValidator implements ConstraintValidator<IAddContenuGlobalCommand, AddContenuGlobalCommand> {
    @Override
    public boolean isValid(AddContenuGlobalCommand addContenuGlobalCommand, ConstraintValidatorContext constraintValidatorContext) {
        return false;
    }
}
