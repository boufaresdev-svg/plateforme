package com.example.BacK.application.g_Vehicule.Command.vehicule.addVehicule;

import com.example.BacK.application.interfaces.g_Vehicule.vehicule.IAddVehiculeCommand;
 import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AddVehiculeValidator implements ConstraintValidator<IAddVehiculeCommand, AddVehiculeCommand> {
    @Override
    public boolean isValid(AddVehiculeCommand command, ConstraintValidatorContext constraintValidatorContext) {
        if (command == null) {
            return false;
        }
        if (command.getSerie () == null || command.getSerie ().isBlank()) {
            throw new IllegalArgumentException("Le nom est obligatoire");
        }

        return true;
    }
}
