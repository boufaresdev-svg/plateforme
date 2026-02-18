package com.example.BacK.application.g_Formation.Command.Evaluation.addEvaluation;


import com.example.BacK.application.interfaces.g_Formation.Evaluation.IAddEvaluationCommand;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AddEvaluationValidator implements ConstraintValidator<IAddEvaluationCommand, AddEvaluationCommand> {
    @Override
    public boolean isValid(AddEvaluationCommand addEvaluationCommand, ConstraintValidatorContext constraintValidatorContext) {
        return false;
    }
}
