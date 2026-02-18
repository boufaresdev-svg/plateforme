package com.example.BacK.application.g_Formation.Command.PlanFormation.updatePlanFormation;

import com.example.BacK.application.interfaces.g_Formation.PlanFormation.IUpdatePlanFormationCommand;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UpdatePlanFormationValidator implements ConstraintValidator<IUpdatePlanFormationCommand, UpdatePlanFormationCommand> {
    @Override
    public boolean isValid(UpdatePlanFormationCommand updatePlanFormationCommand, ConstraintValidatorContext constraintValidatorContext) {
        return false;
    }
}
