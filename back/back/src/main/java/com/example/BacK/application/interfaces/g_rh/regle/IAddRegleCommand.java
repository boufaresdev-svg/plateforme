package com.example.BacK.application.interfaces.g_rh.regle;

import com.example.BacK.application.g_RH.Command.regle.add.AddRegleValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;


@Documented
@Constraint(validatedBy = AddRegleValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface IAddRegleCommand {
    String message() default "Requête update  Regle invalide";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}