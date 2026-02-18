package com.example.BacK.application.interfaces.g_Formation.Type;

import com.example.BacK.application.g_Formation.Command.Domaine.addDomaine.AddDomaineValidator;
import com.example.BacK.application.g_Formation.Command.Type.addType.AddTypeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;


@Documented
@Constraint(validatedBy = AddTypeValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface IAddTypeCommand  {

    String message() default "Requête d'ajout de Type invalide";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};





}
