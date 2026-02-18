package com.example.BacK.application.g_Formation.Command.Categorie.addCategorie;

import com.example.BacK.application.interfaces.g_Formation.Categorie.IAddCategorieCommand;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AddCategorieValidator implements ConstraintValidator<IAddCategorieCommand , AddCategorieCommand> {
    @Override
    public boolean isValid(AddCategorieCommand addCategorieCommand, ConstraintValidatorContext constraintValidatorContext) {
        return false;
    }
}
