package com.example.BacK.application.interfaces.g_Formation.Formateur;

import com.example.BacK.application.g_Formation.Command.Evaluation.updateEvaluation.UpdateEvaluationValidator;
import com.example.BacK.application.g_Formation.Command.Formateur.updateFormateur.UpdateFormateurValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;


@Documented
@Constraint(validatedBy = UpdateFormateurValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface IUpdateFormateurCommand  {

    String message() default "Requête de mise à jour Formateur invalide";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};


}
