package com.example.BacK.application.g_Projet.Command.EmployeAffecte.update;


import com.example.BacK.application.interfaces.g_Projet.EmployeeAffecte.IUpdateEmployeeAffecteCommand;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


public class UpdateEmployeeAffecteValidator implements ConstraintValidator<IUpdateEmployeeAffecteCommand, UpdateEmployeeAffecteCommand> {
    @Override
    public boolean isValid(UpdateEmployeeAffecteCommand command, ConstraintValidatorContext constraintValidatorContext) {
        if (command == null) {
            return false;
        }
        return true;
    }
}