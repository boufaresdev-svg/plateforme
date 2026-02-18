package com.example.BacK.application.interfaces.g_Formation.Formateur;

import com.example.BacK.application.g_Formation.Command.Formateur.addFormateur.AddFormateurValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;


@Documented
@Constraint(validatedBy = AddFormateurValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface IAddFormateurCommand  {

    String message() default "Requête d'ajout de Formateur invalide";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
