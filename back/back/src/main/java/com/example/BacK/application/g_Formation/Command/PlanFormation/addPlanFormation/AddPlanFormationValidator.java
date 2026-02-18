package com.example.BacK.application.g_Formation.Command.PlanFormation.addPlanFormation;

import com.example.BacK.application.interfaces.g_Formation.PlanFormation.IAddPlanFormationCommand;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AddPlanFormationValidator implements ConstraintValidator<IAddPlanFormationCommand, AddPlanFormationCommand> {
    @Override
    public boolean isValid(AddPlanFormationCommand addPlanFormationCommand, ConstraintValidatorContext constraintValidatorContext) {
        return false;
    }
}
