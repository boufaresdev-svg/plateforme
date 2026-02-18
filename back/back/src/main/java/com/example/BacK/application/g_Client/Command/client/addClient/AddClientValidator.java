package com.example.BacK.application.g_Client.Command.client.addClient;

import com.example.BacK.application.interfaces.g_Client.client.IAddClientCommand;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AddClientValidator implements ConstraintValidator<IAddClientCommand, AddClientCommand> {
    @Override
    public boolean isValid(AddClientCommand command, ConstraintValidatorContext constraintValidatorContext) {
        if (command == null) {
            return false;
        }
        return true;
    }
}