package com.example.BacK.application.g_Formation.Command.Evaluation.updateEvaluation;

import com.example.BacK.application.interfaces.g_Formation.Evaluation.IUpdateEvaluationCommand;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UpdateEvaluationValidator implements ConstraintValidator<IUpdateEvaluationCommand, UpdateEvaluationCommand> {
    @Override
    public boolean isValid(UpdateEvaluationCommand updateEvaluationCommand, ConstraintValidatorContext constraintValidatorContext) {
        return false;
    }
}
