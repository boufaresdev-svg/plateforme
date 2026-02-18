package com.example.BacK.application.interfaces.g_Formation.ContenuGlobal;

import com.example.BacK.application.g_Formation.Command.ContenuGlobal.addContenuGlobal.AddContenuGlobalValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;


@Documented
@Constraint(validatedBy = AddContenuGlobalValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface IAddContenuGlobalCommand  {

    String message() default "Requête d'ajout de Contenu Global invalide";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};


}
