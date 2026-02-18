package com.example.BacK.application.g_Vehicule.Command.carteGazole.updateGazoil;


import com.example.BacK.application.interfaces.g_Vehicule.carteGazole.IUpdateCarteGazoilCommand;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


public class UpdateCarteGazoilValidator implements ConstraintValidator<IUpdateCarteGazoilCommand, UpdateCarteGazoilCommand> {
    @Override
    public boolean isValid(UpdateCarteGazoilCommand command, ConstraintValidatorContext constraintValidatorContext) {
        if (command == null) {
            return false;
        }
        return true;
    }
}