package com.example.BacK.application.interfaces.g_Vehicule.Reparation;
import com.example.BacK.application.g_Vehicule.Command.reparation.addReparation.AddReparationValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AddReparationValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface IAddReparationCommand {
    String message() default "Requête d'ajout de réparation invalide";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

