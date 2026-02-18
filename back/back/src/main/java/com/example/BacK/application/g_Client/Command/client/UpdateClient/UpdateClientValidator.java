package com.example.BacK.application.g_Client.Command.client.UpdateClient;

import com.example.BacK.application.interfaces.g_Client.client.IUpdateClientCommand;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UpdateClientValidator implements ConstraintValidator<IUpdateClientCommand, UpdateClientCommand> {
    @Override
    public boolean isValid(UpdateClientCommand command, ConstraintValidatorContext constraintValidatorContext) {
        if (command == null) {
            return false;
        }
        return true;
    }
}