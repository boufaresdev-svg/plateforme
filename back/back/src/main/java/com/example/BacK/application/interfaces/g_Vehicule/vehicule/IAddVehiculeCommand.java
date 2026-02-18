package com.example.BacK.application.interfaces.g_Vehicule.vehicule;

import com.example.BacK.application.g_Vehicule.Command.vehicule.addVehicule.AddVehiculeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AddVehiculeValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface IAddVehiculeCommand {
    String message() default "Requête invalide selon le type";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}