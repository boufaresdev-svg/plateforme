package com.example.BacK.application.interfaces.g_Formation.PlanFormation;

import com.example.BacK.application.g_Formation.Command.PlanFormation.updatePlanFormation.UpdatePlanFormationValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;


@Documented
@Constraint(validatedBy = UpdatePlanFormationValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface IUpdatePlanFormationCommand  {
    String message() default "Requête de mise à jour Plan Formation invalide";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
