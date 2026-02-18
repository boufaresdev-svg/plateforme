package com.example.BacK.application.interfaces.g_Vehicule.vehicule;

import com.example.BacK.application.g_Vehicule.Command.vehicule.UpdateVehicule.UpdateVehiculeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UpdateVehiculeValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface  IUpdateVehiculeCommand {
    String message() default "Requête invalide selon le type";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}