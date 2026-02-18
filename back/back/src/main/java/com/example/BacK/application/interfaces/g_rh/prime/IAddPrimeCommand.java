package com.example.BacK.application.interfaces.g_rh.prime;

import com.example.BacK.application.g_RH.Command.prime.add.AddPrimeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AddPrimeValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface IAddPrimeCommand {
    String message() default "Requête update  Prime invalide";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}