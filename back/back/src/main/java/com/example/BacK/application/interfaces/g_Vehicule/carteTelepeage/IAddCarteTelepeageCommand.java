package com.example.BacK.application.interfaces.g_Vehicule.carteTelepeage;

 import com.example.BacK.application.g_Vehicule.Command.carteTelepeage.addCarteTelepeage.AddCarteTelepeageValidator;
 import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AddCarteTelepeageValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface IAddCarteTelepeageCommand {
    String message() default "Requête d'ajout de carte carburant invalide";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
