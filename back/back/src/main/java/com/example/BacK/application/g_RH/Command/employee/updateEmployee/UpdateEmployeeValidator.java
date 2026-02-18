package com.example.BacK.application.g_RH.Command.employee.updateEmployee;

import com.example.BacK.application.interfaces.g_rh.employee.IUpdateEmployeeCommand;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


public class UpdateEmployeeValidator implements ConstraintValidator<IUpdateEmployeeCommand, UpdateEmployeeCommand> {
    @Override
    public boolean isValid(UpdateEmployeeCommand command, ConstraintValidatorContext constraintValidatorContext) {
        if (command == null) {
            return false;
        }
        if (command.getNom () == null ) {
            throw new IllegalArgumentException("Le nom est obligatoire");
        }

        return true;
    }
}