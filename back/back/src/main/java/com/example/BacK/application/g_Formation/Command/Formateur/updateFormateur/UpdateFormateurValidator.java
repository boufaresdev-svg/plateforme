package com.example.BacK.application.g_Formation.Command.Formateur.updateFormateur;

import com.example.BacK.application.interfaces.g_Formation.Formateur.IUpdateFormateurCommand;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UpdateFormateurValidator implements ConstraintValidator<IUpdateFormateurCommand, UpdateFormateurCommand> {
    @Override
    public boolean isValid(UpdateFormateurCommand updateFormateurCommand, ConstraintValidatorContext constraintValidatorContext) {
        return false;
    }
}
