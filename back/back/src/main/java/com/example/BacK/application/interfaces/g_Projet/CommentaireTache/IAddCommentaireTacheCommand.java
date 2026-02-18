package com.example.BacK.application.interfaces.g_Projet.CommentaireTache;

 import com.example.BacK.application.g_Projet.Command.commentaireTache.add.AddCommentaireTacheValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AddCommentaireTacheValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface IAddCommentaireTacheCommand {
    String message() default "Requête d'ajout de Charge invalide";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
