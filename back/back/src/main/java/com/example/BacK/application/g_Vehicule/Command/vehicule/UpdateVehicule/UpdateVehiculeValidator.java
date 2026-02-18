package com.example.BacK.application.g_Vehicule.Command.vehicule.UpdateVehicule;

import com.example.BacK.application.interfaces.g_Vehicule.vehicule.IUpdateVehiculeCommand;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UpdateVehiculeValidator implements ConstraintValidator<IUpdateVehiculeCommand, UpdateVehiculeCommand> {
    @Override
    public boolean isValid(UpdateVehiculeCommand command, ConstraintValidatorContext constraintValidatorContext) {

        if (command == null) {
            return false;
        }
        if (command.getSerie() == null || command.getSerie().isBlank()) {
            throw new IllegalArgumentException("Le nom est obligatoire");
        }

        return true;
    }

}
