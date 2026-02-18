package com.example.BacK.application.interfaces.g_Projet.tache;

import com.example.BacK.application.g_Projet.Command.Tache.add.AddTacheValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AddTacheValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface IAddTacheCommand {
    String message() default "Requête d'ajout de tache invalide";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}