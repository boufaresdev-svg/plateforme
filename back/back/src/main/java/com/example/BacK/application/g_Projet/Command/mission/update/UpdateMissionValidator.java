package com.example.BacK.application.g_Projet.Command.mission.update;


import com.example.BacK.application.interfaces.g_Projet.mission.IUpdateMissionCommand;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UpdateMissionValidator implements ConstraintValidator<IUpdateMissionCommand, UpdateMissionCommand> {
    @Override
    public boolean isValid(UpdateMissionCommand command, ConstraintValidatorContext constraintValidatorContext) {
        if (command == null) {
            return false;
        }
        return true;
    }
}