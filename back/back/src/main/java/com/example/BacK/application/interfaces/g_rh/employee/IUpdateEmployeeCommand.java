package com.example.BacK.application.interfaces.g_rh.employee;

import com.example.BacK.application.g_RH.Command.employee.updateEmployee.UpdateEmployeeValidator;
import com.example.BacK.application.g_Vehicule.Command.carteGazole.updateGazoil.UpdateCarteGazoilValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;
@Documented
@Constraint(validatedBy = UpdateEmployeeValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface IUpdateEmployeeCommand {
    String message() default "Requête de mise à jour de Employee invalide";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
