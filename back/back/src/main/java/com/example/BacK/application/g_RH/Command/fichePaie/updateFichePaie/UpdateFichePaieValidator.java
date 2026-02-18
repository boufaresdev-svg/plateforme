package com.example.BacK.application.g_RH.Command.fichePaie.updateFichePaie;

import com.example.BacK.application.interfaces.g_rh.fichePaie.IUpdateFichePaieCommand;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UpdateFichePaieValidator implements ConstraintValidator<IUpdateFichePaieCommand, UpdateFichePaieCommand> {
    @Override
    public boolean isValid(UpdateFichePaieCommand command, ConstraintValidatorContext constraintValidatorContext) {
        if (command == null) {
            return false;
        }
        return true;
    }
}
