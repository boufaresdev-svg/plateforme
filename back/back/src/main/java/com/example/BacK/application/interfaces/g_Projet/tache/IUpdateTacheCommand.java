package com.example.BacK.application.interfaces.g_Projet.tache;

import com.example.BacK.application.g_Projet.Command.Tache.update.UpdateTacheValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UpdateTacheValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface IUpdateTacheCommand {
    String message() default "Requête d'ajout de tache invalide";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
