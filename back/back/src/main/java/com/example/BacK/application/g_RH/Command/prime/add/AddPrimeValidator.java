package com.example.BacK.application.g_RH.Command.prime.add;

import com.example.BacK.application.interfaces.g_rh.prime.IAddPrimeCommand;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


public class AddPrimeValidator implements ConstraintValidator<IAddPrimeCommand, AddPrimeCommand> {
    @Override
    public boolean isValid(AddPrimeCommand command, ConstraintValidatorContext constraintValidatorContext) {
        if (command == null) {
            return false;
        }
        return true;
    }
}
