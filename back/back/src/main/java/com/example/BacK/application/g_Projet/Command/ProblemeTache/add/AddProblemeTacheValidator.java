package com.example.BacK.application.g_Projet.Command.ProblemeTache.add;

import com.example.BacK.application.interfaces.g_Projet.ProblemeTache.IAddProblemeTacheCommand;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


public class AddProblemeTacheValidator implements ConstraintValidator<IAddProblemeTacheCommand, AddProblemeTacheCommand> {
    @Override
    public boolean isValid(AddProblemeTacheCommand command, ConstraintValidatorContext constraintValidatorContext) {
        if (command == null) {
            return false;
        }
        return true;
    }
}