package com.example.BacK.application.interfaces.g_Formation.Evaluation;

import com.example.BacK.application.g_Formation.Command.Evaluation.updateEvaluation.UpdateEvaluationValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;


@Documented
@Constraint(validatedBy = UpdateEvaluationValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface IUpdateEvaluationCommand  {

    String message() default "Requête de mise à jour Evaluation invalide";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
