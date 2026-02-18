package com.example.BacK.application.g_Projet.Command.projet.add;

import com.example.BacK.application.interfaces.g_Projet.projet.IAddPrrojetCommand;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


public class AddProjetValidator implements ConstraintValidator<IAddPrrojetCommand, AddProjetCommand> {
    @Override
    public boolean isValid(AddProjetCommand command, ConstraintValidatorContext constraintValidatorContext) {
        if (command == null) {
            return false;
        }
        return true;
    }
}