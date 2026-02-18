package com.example.BacK.application.interfaces.g_rh.congee;

import com.example.BacK.application.g_RH.Command.congee.addCongee.AddCongeeValidator;
import com.example.BacK.application.g_Vehicule.Command.carteGazole.addGazoil.AddCarteGazoilValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AddCongeeValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface IAddCongeeCommand {
    String message() default "Requête d'ajout deCongee invalide";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
