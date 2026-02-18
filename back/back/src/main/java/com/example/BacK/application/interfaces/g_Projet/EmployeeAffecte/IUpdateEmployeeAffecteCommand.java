package com.example.BacK.application.interfaces.g_Projet.EmployeeAffecte;

import com.example.BacK.application.g_Projet.Command.EmployeAffecte.update.UpdateEmployeeAffecteValidator;
import com.example.BacK.application.g_Projet.Command.commentaireTache.update.UpdateCommentaireTacheValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UpdateEmployeeAffecteValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface IUpdateEmployeeAffecteCommand {
    String message() default "Requête d'ajout de Charge invalide";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
