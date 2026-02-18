package com.example.BacK.application.interfaces.g_Formation.Certificat;


import com.example.BacK.application.g_Formation.Command.Certificat.updateCertificat.UpdateCertificatValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UpdateCertificatValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface IUpdateCertificatCommand  {

    String message() default "Requête de mise à jour Certificat invalide";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};


}
