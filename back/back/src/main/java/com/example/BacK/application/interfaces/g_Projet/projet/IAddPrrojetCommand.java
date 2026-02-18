package com.example.BacK.application.interfaces.g_Projet.projet;

import com.example.BacK.application.g_Projet.Command.commentaireTache.add.AddCommentaireTacheValidator;
import com.example.BacK.application.g_Projet.Command.projet.add.AddProjetValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;


@Documented
@Constraint(validatedBy = AddProjetValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface IAddPrrojetCommand {
    String message() default "Requête d'ajout de Charge invalide";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
