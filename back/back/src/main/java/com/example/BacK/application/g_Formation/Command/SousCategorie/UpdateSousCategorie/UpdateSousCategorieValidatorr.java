package com.example.BacK.application.g_Formation.Command.SousCategorie.UpdateSousCategorie;


import com.example.BacK.application.interfaces.g_Formation.SousCategorie.IUpdateSousCategorieCommand;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UpdateSousCategorieValidatorr  implements ConstraintValidator<IUpdateSousCategorieCommand, UpdateSousCategorieCommand> {

    @Override
    public boolean isValid(UpdateSousCategorieCommand updateSousCategorieCommand, ConstraintValidatorContext constraintValidatorContext) {
        return false;
    }
}
