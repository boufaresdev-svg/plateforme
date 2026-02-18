package com.example.BacK.application.interfaces.g_Formation.SousCategorie;

import com.example.BacK.application.g_Formation.Command.SousCategorie.addSousCategorie.AddSousCategorieValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;


@Documented
@Constraint(validatedBy = AddSousCategorieValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface IAddSousCategorieCommand {

    String message() default "Requête d'ajout de SousCategorie invalide";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
