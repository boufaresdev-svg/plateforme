package com.example.BacK.application.interfaces.g_Formation.ObjectifSpecifique;

import com.example.BacK.application.g_Formation.Command.ContenuGlobal.updateContenuGlobal.UpdateContenuGlobalValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;


@Documented
@Constraint(validatedBy = UpdateContenuGlobalValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface IUpdateObjectifSpecifiqueCommand {

    String message() default "Requête de mise à jour Objectif Specifique invalide";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
