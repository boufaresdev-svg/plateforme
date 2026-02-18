package com.example.BacK.application.interfaces.g_rh.prime;


import com.example.BacK.application.g_RH.Command.prime.update.UpdatePrimeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UpdatePrimeValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface IUpdatePrimeCommand {
    String message() default "Requête update  Prime invalide";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}