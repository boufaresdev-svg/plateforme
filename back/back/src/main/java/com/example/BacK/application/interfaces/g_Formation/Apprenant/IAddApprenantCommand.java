package com.example.BacK.application.interfaces.g_Formation.Apprenant;

import com.example.BacK.application.g_Formation.Command.Apprenant.addApprenant.AddApprenantValidator;
import com.example.BacK.application.g_Formation.Command.Categorie.addCategorie.AddCategorieValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AddApprenantValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface IAddApprenantCommand {

    String message() default "Requête d'ajout de Apprenant invalide";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};


}
