package com.example.BacK.application.interfaces.g_rh.fichePaie;

import com.example.BacK.application.g_RH.Command.fichePaie.updateFichePaie.UpdateFichePaieValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UpdateFichePaieValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface IUpdateFichePaieCommand {
    String message() default "Requête update  FichePaie invalide";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}