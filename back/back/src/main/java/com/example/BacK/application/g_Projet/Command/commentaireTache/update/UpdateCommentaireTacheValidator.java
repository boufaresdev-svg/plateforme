package com.example.BacK.application.g_Projet.Command.commentaireTache.update;

import com.example.BacK.application.interfaces.g_Projet.CommentaireTache.IUpdateCommentaireTacheCommand;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UpdateCommentaireTacheValidator implements ConstraintValidator<IUpdateCommentaireTacheCommand, UpdateCommentaireTacheCommand> {
    @Override
    public boolean isValid(UpdateCommentaireTacheCommand command, ConstraintValidatorContext constraintValidatorContext) {
        if (command == null) {
            return false;
        }
        return true;
    }
}