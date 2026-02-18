package com.example.BacK.application.interfaces.g_Formation.SousCategorie;

import com.example.BacK.application.g_Formation.Command.SousCategorie.UpdateSousCategorie.UpdateSousCategorieValidatorr;
import com.example.BacK.application.g_Formation.Command.SousCategorie.addSousCategorie.AddSousCategorieValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UpdateSousCategorieValidatorr.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)

public @interface IUpdateSousCategorieCommand {

    String message() default "Requête de mise à jour SousCategorie invalide";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
