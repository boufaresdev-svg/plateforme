package com.example.BacK.application.interfaces.g_Projet.ProblemeTache;

import com.example.BacK.application.g_Projet.Command.ProblemeTache.update.UpdateProblemeTacheValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UpdateProblemeTacheValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface IUpdateProblemeTacheCommand {
    String message() default "Requête d'ajout de Charge invalide";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
