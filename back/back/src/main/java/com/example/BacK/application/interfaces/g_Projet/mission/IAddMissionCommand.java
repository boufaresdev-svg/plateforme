package com.example.BacK.application.interfaces.g_Projet.mission;

import com.example.BacK.application.g_Projet.Command.commentaireTache.add.AddCommentaireTacheValidator;
import com.example.BacK.application.g_Projet.Command.mission.add.AddMissionValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AddMissionValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface IAddMissionCommand {
    String message() default "Requête d'ajout de Charge invalide";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
