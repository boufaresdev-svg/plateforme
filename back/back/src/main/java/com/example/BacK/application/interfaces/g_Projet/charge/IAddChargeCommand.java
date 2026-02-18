package com.example.BacK.application.interfaces.g_Projet.charge;

import com.example.BacK.application.g_Projet.Command.charge.add.AddChargeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;



@Documented
@Constraint(validatedBy = AddChargeValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface IAddChargeCommand {
    String message() default "Requête d'ajout de Charge invalide";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
