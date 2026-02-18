package com.example.BacK.application.g_Formation.Command.ObjectifSpecifique.addObjectifSpecifique;

import com.example.BacK.application.interfaces.g_Formation.ObjectifSpecifique.IAddObjectifSpecifiqueCommand;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AddObjectifSpecifiqueValidator implements ConstraintValidator<IAddObjectifSpecifiqueCommand, AddObjectifSpecifiqueCommand> {
    @Override
    public boolean isValid(AddObjectifSpecifiqueCommand addObjectifSpecifiqueCommand, ConstraintValidatorContext constraintValidatorContext) {
        return false;
    }
}
