package com.example.BacK.application.interfaces.g_Vehicule.carteTelepeage;
import com.example.BacK.application.g_Vehicule.Command.carteGazole.updateGazoil.UpdateCarteGazoilValidator;
import com.example.BacK.application.g_Vehicule.Command.carteTelepeage.updateCarteTelepeage.UpdateCarteTelepeageValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;
@Documented
@Constraint(validatedBy = UpdateCarteTelepeageValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface IUpdateCarteTelepeageCommand {
    String message() default "Requête de mise à jour de carte carburant invalide";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
