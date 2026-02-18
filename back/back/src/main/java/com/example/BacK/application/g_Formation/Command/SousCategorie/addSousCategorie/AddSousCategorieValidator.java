package com.example.BacK.application.g_Formation.Command.SousCategorie.addSousCategorie;

import com.example.BacK.application.interfaces.g_Formation.SousCategorie.IAddSousCategorieCommand;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AddSousCategorieValidator  implements ConstraintValidator<IAddSousCategorieCommand, AddSousCategorieCommand> {
    @Override
    public boolean isValid(AddSousCategorieCommand addSousCategorieCommand, ConstraintValidatorContext constraintValidatorContext) {
        return false;
    }
}
