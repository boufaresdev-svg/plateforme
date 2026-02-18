package com.example.BacK.application.g_Formation.Command.Apprenant.updateApprenant;

import com.example.BacK.application.interfaces.g_Formation.Apprenant.IUpdateApprenantCommand;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UpdateApprenantValidator implements ConstraintValidator<IUpdateApprenantCommand, UpdateApprenantCommand> {
    @Override
    public boolean isValid(UpdateApprenantCommand updateApprenantCommand, ConstraintValidatorContext constraintValidatorContext) {
        return false;
    }
}
