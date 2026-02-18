package com.example.BacK.application.g_RH.Command.fichePaie.addFichePaie;

import com.example.BacK.application.interfaces.g_rh.fichePaie.IAddFichePaieCommand;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AddFichepaieValidator implements ConstraintValidator<IAddFichePaieCommand, AddFichePaieCommand> {
    @Override
    public boolean isValid(AddFichePaieCommand command, ConstraintValidatorContext constraintValidatorContext) {
        if (command == null) {
            return false;
        }
        return true;
    }
}
