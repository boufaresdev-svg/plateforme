package com.example.BacK.application.interfaces.g_Formation.Domaine;

import com.example.BacK.application.g_Formation.Command.Certificat.addCertificat.AddCertificatValidator;
import com.example.BacK.application.g_Formation.Command.Domaine.addDomaine.AddDomaineValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;


@Documented
@Constraint(validatedBy = AddDomaineValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface IAddDomaineCommand  {

    String message() default "Requête d'ajout de Domaine invalide";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
