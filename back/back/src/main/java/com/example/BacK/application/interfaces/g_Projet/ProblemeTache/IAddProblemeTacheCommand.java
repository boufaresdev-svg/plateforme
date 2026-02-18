package com.example.BacK.application.interfaces.g_Projet.ProblemeTache;

import com.example.BacK.application.g_Projet.Command.ProblemeTache.add.AddProblemeTacheValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;



@Documented
@Constraint(validatedBy = AddProblemeTacheValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface IAddProblemeTacheCommand {
    String message() default "Requête d'ajout de Charge invalide";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
