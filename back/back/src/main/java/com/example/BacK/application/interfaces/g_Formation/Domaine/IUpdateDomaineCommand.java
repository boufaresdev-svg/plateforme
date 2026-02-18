package com.example.BacK.application.interfaces.g_Formation.Domaine;

import com.example.BacK.application.g_Formation.Command.Domaine.updateDomaine.UpdateDomaineValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;


@Documented
@Constraint(validatedBy = UpdateDomaineValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)

public @interface IUpdateDomaineCommand  {

    String message() default "Requête de mise à jour Domaine invalide";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
