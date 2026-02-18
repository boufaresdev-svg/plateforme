package com.example.BacK.application.g_Formation.Command.Examen.updateExamen;

import com.example.BacK.application.interfaces.g_Formation.Examen.IUpdateExamenCommand;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UpdateExamenValidator implements ConstraintValidator<IUpdateExamenCommand, UpdateExamenCommand> {
    @Override
    public boolean isValid(UpdateExamenCommand updateExamenCommand, ConstraintValidatorContext constraintValidatorContext) {
        return false;
    }
}
