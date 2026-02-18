package com.example.BacK.application.g_Formation.Command.Categorie.updateCategorie;

import com.example.BacK.application.interfaces.g_Formation.Categorie.IUpdateCategorieCommand;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UpdateCategorieValidator implements ConstraintValidator<IUpdateCategorieCommand, UpdateCategorieCommand> {
    @Override
    public boolean isValid(UpdateCategorieCommand updateCategorieCommand, ConstraintValidatorContext constraintValidatorContext) {
        return false;
    }
}
