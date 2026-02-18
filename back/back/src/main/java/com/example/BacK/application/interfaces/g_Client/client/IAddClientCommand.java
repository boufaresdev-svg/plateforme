package com.example.BacK.application.interfaces.g_Client.client;

import com.example.BacK.application.g_Client.Command.client.addClient.AddClientValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AddClientValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface IAddClientCommand {
    String message() default "Requête d'ajout de Client invalide";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}