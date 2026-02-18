package com.example.BacK.application.interfaces.g_Formation.PlanFormation;

import com.example.BacK.application.g_Formation.Command.PlanFormation.addPlanFormation.AddPlanFormationValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;


@Documented
@Constraint(validatedBy = AddPlanFormationValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface IAddPlanFormationCommand {

    String message() default "Requête d'ajout de Plan Formation invalide";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};



}
