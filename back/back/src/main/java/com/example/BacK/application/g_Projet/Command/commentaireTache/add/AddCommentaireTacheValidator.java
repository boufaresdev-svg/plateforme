package com.example.BacK.application.g_Projet.Command.commentaireTache.add;

import com.example.BacK.application.interfaces.g_Projet.CommentaireTache.IAddCommentaireTacheCommand;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


public class AddCommentaireTacheValidator implements ConstraintValidator<IAddCommentaireTacheCommand, AddCommentaireTacheCommand> {
    @Override
    public boolean isValid(AddCommentaireTacheCommand command, ConstraintValidatorContext constraintValidatorContext) {
        if (command == null) {
            return false;
        }
        return true;
    }
}