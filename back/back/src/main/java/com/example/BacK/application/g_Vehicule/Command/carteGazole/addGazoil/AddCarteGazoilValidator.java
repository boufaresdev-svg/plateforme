package com.example.BacK.application.g_Vehicule.Command.carteGazole.addGazoil;

import com.example.BacK.application.interfaces.g_Vehicule.carteGazole.IAddCarteGazoilCommand;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AddCarteGazoilValidator implements ConstraintValidator<IAddCarteGazoilCommand, AddCarteGazoilCommand> {
    @Override
    public boolean isValid(AddCarteGazoilCommand command, ConstraintValidatorContext constraintValidatorContext) {
        if (command == null) {
            return false;
        }
        return true;
    }
}