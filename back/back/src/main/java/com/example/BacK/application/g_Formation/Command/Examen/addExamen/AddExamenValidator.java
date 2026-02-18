package com.example.BacK.application.g_Formation.Command.Examen.addExamen;

import com.example.BacK.application.interfaces.g_Formation.Examen.IAddExamenCommand;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AddExamenValidator implements ConstraintValidator<IAddExamenCommand, AddExamenCommand> {
    @Override
    public boolean isValid(AddExamenCommand addExamenCommand, ConstraintValidatorContext constraintValidatorContext) {
        return false;
    }
}
