package com.example.BacK.application.g_Projet.Command.mission.add;

import com.example.BacK.application.interfaces.g_Projet.mission.IAddMissionCommand;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


public class AddMissionValidator implements ConstraintValidator<IAddMissionCommand, AddMissionCommand> {
    @Override
    public boolean isValid(AddMissionCommand command, ConstraintValidatorContext constraintValidatorContext) {
        if (command == null) {
            return false;
        }
        return true;
    }
}