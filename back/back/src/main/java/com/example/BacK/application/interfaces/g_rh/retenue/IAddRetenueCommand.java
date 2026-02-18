package com.example.BacK.application.interfaces.g_rh.retenue;


import com.example.BacK.application.g_RH.Command.retenue.add.AddRetenueValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;


@Documented
@Constraint(validatedBy = AddRetenueValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface IAddRetenueCommand {
    String message() default "Requête update  Reteune invalide";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}