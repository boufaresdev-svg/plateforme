package com.example.BacK.application.interfaces.g_Formation.Examen;

import com.example.BacK.application.g_Formation.Command.Examen.updateExamen.UpdateExamenValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;


@Documented
@Constraint(validatedBy = UpdateExamenValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface IUpdateExamenCommand  {

    String message() default "Requête de mise à jour Examen invalide";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
