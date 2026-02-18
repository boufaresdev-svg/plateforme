package com.example.BacK.application.interfaces.g_Formation.Apprenant;

import com.example.BacK.application.g_Formation.Command.Apprenant.updateApprenant.UpdateApprenantValidator;
import com.example.BacK.application.g_Formation.Command.Categorie.updateCategorie.UpdateCategorieValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;


@Documented
@Constraint(validatedBy = UpdateApprenantValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface IUpdateApprenantCommand {

    String message() default "Requête de mise à jour Apprenant invalide";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};



}
