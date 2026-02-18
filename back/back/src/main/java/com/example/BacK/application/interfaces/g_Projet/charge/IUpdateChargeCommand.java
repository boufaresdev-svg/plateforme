package com.example.BacK.application.interfaces.g_Projet.charge;

 import com.example.BacK.application.g_Projet.Command.charge.update.UpdateChargeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UpdateChargeValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface IUpdateChargeCommand {
    String message() default "Requête de mise à jour  de Charge invalide";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
