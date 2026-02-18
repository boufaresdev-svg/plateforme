package com.example.BacK.application.interfaces.g_rh.retenue;


import com.example.BacK.application.g_RH.Command.retenue.update.UpdateRetenueValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UpdateRetenueValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface IUpdateRetenueCommand {
    String message() default "Requête update  Reteune invalide";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}