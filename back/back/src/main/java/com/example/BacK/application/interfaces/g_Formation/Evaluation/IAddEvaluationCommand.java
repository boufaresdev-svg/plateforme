package com.example.BacK.application.interfaces.g_Formation.Evaluation;

import com.example.BacK.application.g_Formation.Command.Evaluation.addEvaluation.AddEvaluationValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AddEvaluationValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface IAddEvaluationCommand {
    String message() default "Requête d'ajout de Evaluation invalide";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};


}
