package com.example.BacK.application.interfaces.g_rh.congee;

import com.example.BacK.application.g_RH.Command.congee.updateCongee.UpdateCongeeValidator;
import com.example.BacK.application.g_Vehicule.Command.carteGazole.updateGazoil.UpdateCarteGazoilValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;
@Documented
@Constraint(validatedBy = UpdateCongeeValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface IUpdateCongeeCommand {
    String message() default "Requête de mise à jour  de Congee invalide";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
