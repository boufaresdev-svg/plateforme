package com.example.BacK.application.interfaces.g_Projet.phase;

 import com.example.BacK.application.g_Projet.Command.phase.add.AddPhaseValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AddPhaseValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface IAddPhaseCommand {
    String message() default "Requête d'ajout de Charge invalide";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
