package com.example.BacK.application.interfaces.g_Formation.Examen;

import com.example.BacK.application.g_Formation.Command.Examen.addExamen.AddExamenValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;


@Documented
@Constraint(validatedBy = AddExamenValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface IAddExamenCommand  {

    String message() default "Requête d'ajout de Examen invalide";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
