package com.example.BacK.application.interfaces.g_Formation.ContenuJourFormation;

import com.example.BacK.application.g_Formation.Command.ContenuJourFormation.addContenuJourFormation.AddContenuJourFormationValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;


@Documented
@Constraint(validatedBy = AddContenuJourFormationValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)

public @interface IAddContenuJourFormationCommand  {

    String message() default "Requête d'ajout de Contenu Jour Formation invalide";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
