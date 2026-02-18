package com.example.BacK.application.interfaces.g_Formation.Type;

import com.example.BacK.application.g_Formation.Command.Type.updateType.UpdateTypeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;


@Documented
@Constraint(validatedBy = UpdateTypeValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface IUpdateTypeCommand  {

    String message() default "Requête de mise à jour Type invalide";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
