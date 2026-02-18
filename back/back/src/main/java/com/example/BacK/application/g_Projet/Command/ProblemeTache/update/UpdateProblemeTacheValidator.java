package com.example.BacK.application.g_Projet.Command.ProblemeTache.update;

import com.example.BacK.application.interfaces.g_Projet.ProblemeTache.IUpdateProblemeTacheCommand;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UpdateProblemeTacheValidator implements ConstraintValidator<IUpdateProblemeTacheCommand, UpdateProblemeTacheCommand> {
    @Override
    public boolean isValid(UpdateProblemeTacheCommand command, ConstraintValidatorContext constraintValidatorContext) {
        if (command == null) {
            return false;
        }
        return true;
    }
}