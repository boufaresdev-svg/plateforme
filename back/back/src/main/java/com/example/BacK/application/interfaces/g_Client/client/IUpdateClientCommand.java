package com.example.BacK.application.interfaces.g_Client.client;

import com.example.BacK.application.g_Client.Command.client.UpdateClient.UpdateClientValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UpdateClientValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface IUpdateClientCommand {
    String message() default "Requête d'modifier de Client invalide";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}