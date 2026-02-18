package com.example.BacK.application.interfaces.g_rh.employee;

import com.example.BacK.application.g_RH.Command.employee.addEmployee.AddEmployeeValidator;
import com.example.BacK.application.g_Vehicule.Command.carteGazole.addGazoil.AddCarteGazoilValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AddEmployeeValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface IAddEmployeeCommand {
    String message() default "Requête d'ajout de Employee invalide";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
