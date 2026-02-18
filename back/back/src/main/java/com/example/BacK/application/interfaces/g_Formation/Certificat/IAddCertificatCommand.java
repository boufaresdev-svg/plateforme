package com.example.BacK.application.interfaces.g_Formation.Certificat;


import com.example.BacK.application.g_Formation.Command.Certificat.addCertificat.AddCertificatValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AddCertificatValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface IAddCertificatCommand  {
    String message() default "Requête d'ajout de Certificat invalide";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
