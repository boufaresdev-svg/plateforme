package com.example.BacK.application.interfaces.g_Vehicule.Reparation;
import com.example.BacK.application.g_Vehicule.Command.reparation.updateReparation.UpdateReparationValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UpdateReparationValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface IUpdateReparationCommand {
    String message() default "Requête de mise à jour de réparation invalide";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

