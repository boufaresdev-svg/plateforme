package com.example.BacK.application.interfaces.g_Formation.ContenuJourFormation;

import com.example.BacK.application.g_Formation.Command.ContenuJourFormation.updateContenuJourFormation.UpdateContenuJourFormationValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UpdateContenuJourFormationValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface IUpdateContenuJourFormationCommand  {

    String message() default "Requête de mise à jour Contenu Jour Formation  invalide";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};



}
