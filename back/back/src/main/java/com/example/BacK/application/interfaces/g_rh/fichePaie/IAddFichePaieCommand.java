package com.example.BacK.application.interfaces.g_rh.fichePaie;


import com.example.BacK.application.g_RH.Command.fichePaie.addFichePaie.AddFichepaieValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AddFichepaieValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface IAddFichePaieCommand {
    String message() default "Requête d'ajout  FichePaie invalide";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
