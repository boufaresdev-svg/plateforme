package com.example.BacK.application.interfaces.g_Projet.mission;

import com.example.BacK.application.g_Projet.Command.commentaireTache.update.UpdateCommentaireTacheValidator;
import com.example.BacK.application.g_Projet.Command.mission.update.UpdateMissionValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;


@Documented
@Constraint(validatedBy = UpdateMissionValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface IUpdateMissionCommand {
    String message() default "Requête d'ajout de Charge invalide";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
