package com.example.BacK.application.g_Projet.Command.EmployeAffecte.add;

import com.example.BacK.application.interfaces.g_Projet.EmployeeAffecte.IAddEmployeeAffecteCommand;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


public class AddEmployeeAffecteValidator implements ConstraintValidator<IAddEmployeeAffecteCommand,AddEmployeeAffecteCommand> {
    @Override
    public boolean isValid(AddEmployeeAffecteCommand command, ConstraintValidatorContext constraintValidatorContext) {
        if (command == null) {
            return false;
        }
        return true;
    }
}