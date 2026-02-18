package com.example.BacK.application.g_RH.Command.employee.addEmployee;

import com.example.BacK.application.interfaces.g_rh.employee.IAddEmployeeCommand;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


public class AddEmployeeValidator implements ConstraintValidator<IAddEmployeeCommand, AddEmployeeCommand> {
    @Override
    public boolean isValid(AddEmployeeCommand command, ConstraintValidatorContext constraintValidatorContext) {
        if (command == null) {
            return false;
        }
        if (command.getNom () == null ) {
            throw new IllegalArgumentException("Le nom est obligatoire");
        }

        return true;
    }
}