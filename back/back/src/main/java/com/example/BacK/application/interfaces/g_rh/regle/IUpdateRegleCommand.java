package com.example.BacK.application.interfaces.g_rh.regle;

import com.example.BacK.application.g_RH.Command.regle.update.UpdateRegleValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;


@Documented
@Constraint(validatedBy = UpdateRegleValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface IUpdateRegleCommand {
    String message() default "Requête update  Regle invalide";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}