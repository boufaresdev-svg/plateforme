package com.example.BacK.application.interfaces.g_Vehicule.carteGazole;

import com.example.BacK.application.g_Vehicule.Command.carteGazole.addGazoil.AddCarteGazoilValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AddCarteGazoilValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface IAddCarteGazoilCommand {
    String message() default "Requête d'ajout de carte carburant invalide";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
