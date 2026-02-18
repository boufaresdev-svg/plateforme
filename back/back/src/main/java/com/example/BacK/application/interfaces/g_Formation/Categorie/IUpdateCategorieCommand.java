package com.example.BacK.application.interfaces.g_Formation.Categorie;

import com.example.BacK.application.g_Formation.Command.Categorie.updateCategorie.UpdateCategorieValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;


@Documented
@Constraint(validatedBy = UpdateCategorieValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface IUpdateCategorieCommand  {

    String message() default "Requête de mise à jour Categorie invalide";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
