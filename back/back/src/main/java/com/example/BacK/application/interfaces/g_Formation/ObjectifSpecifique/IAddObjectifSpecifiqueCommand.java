package com.example.BacK.application.interfaces.g_Formation.ObjectifSpecifique;

import com.example.BacK.application.g_Formation.Command.ContenuGlobal.addContenuGlobal.AddContenuGlobalValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;


@Documented
@Constraint(validatedBy = AddContenuGlobalValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface IAddObjectifSpecifiqueCommand  {

    String message() default "Requête d'ajout de Objectif Specifique invalide";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};




}
