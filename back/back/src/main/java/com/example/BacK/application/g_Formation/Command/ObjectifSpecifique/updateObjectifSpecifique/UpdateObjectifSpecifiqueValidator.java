package com.example.BacK.application.g_Formation.Command.ObjectifSpecifique.updateObjectifSpecifique;

import com.example.BacK.application.interfaces.g_Formation.ObjectifSpecifique.IUpdateObjectifSpecifiqueCommand;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UpdateObjectifSpecifiqueValidator  implements ConstraintValidator<IUpdateObjectifSpecifiqueCommand, UpdateObjectifSpecifiqueCommand> {
    @Override
    public boolean isValid(UpdateObjectifSpecifiqueCommand updateObjectifSpecifiqueCommand, ConstraintValidatorContext constraintValidatorContext) {
        return false;
    }
}
