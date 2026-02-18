package com.example.BacK.application.interfaces.g_Projet.EmployeeAffecte;

import com.example.BacK.application.g_Projet.Command.EmployeAffecte.add.AddEmployeeAffecteValidator;
import com.example.BacK.application.g_Projet.Command.commentaireTache.add.AddCommentaireTacheValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;



@Documented
@Constraint(validatedBy = AddEmployeeAffecteValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface IAddEmployeeAffecteCommand {
    String message() default "Requête d'ajout de Charge invalide";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
