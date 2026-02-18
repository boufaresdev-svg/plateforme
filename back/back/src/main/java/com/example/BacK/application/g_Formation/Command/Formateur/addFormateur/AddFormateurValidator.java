package com.example.BacK.application.g_Formation.Command.Formateur.addFormateur;

import com.example.BacK.application.interfaces.g_Formation.Formateur.IAddFormateurCommand;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AddFormateurValidator implements ConstraintValidator<IAddFormateurCommand, AddFormateurCommand> {
    @Override
    public boolean isValid(AddFormateurCommand addFormateurCommand, ConstraintValidatorContext constraintValidatorContext) {
        return false;
    }
}
