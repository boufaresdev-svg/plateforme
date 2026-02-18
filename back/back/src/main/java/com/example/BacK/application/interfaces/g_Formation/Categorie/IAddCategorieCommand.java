package com.example.BacK.application.interfaces.g_Formation.Categorie;

import com.example.BacK.application.g_Formation.Command.Categorie.addCategorie.AddCategorieValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AddCategorieValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface IAddCategorieCommand {

    String message() default "Requête d'ajout de Categorie invalide";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
