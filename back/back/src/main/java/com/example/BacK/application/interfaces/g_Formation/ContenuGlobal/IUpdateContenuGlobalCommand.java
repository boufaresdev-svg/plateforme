package com.example.BacK.application.interfaces.g_Formation.ContenuGlobal;

import com.example.BacK.application.g_Formation.Command.Categorie.updateCategorie.UpdateCategorieValidator;
import com.example.BacK.application.g_Formation.Command.ContenuGlobal.updateContenuGlobal.UpdateContenuGlobalValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;


@Documented
@Constraint(validatedBy = UpdateContenuGlobalValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface IUpdateContenuGlobalCommand  {

    String message() default "Requête de mise à jour Contenu Global invalide";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};


}
