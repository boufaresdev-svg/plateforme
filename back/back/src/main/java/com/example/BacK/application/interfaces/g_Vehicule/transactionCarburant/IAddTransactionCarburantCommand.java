package com.example.BacK.application.interfaces.g_Vehicule.transactionCarburant;
import com.example.BacK.application.g_Vehicule.Command.transactionCarburant.addTransaction.AddTransactionCarubrantValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AddTransactionCarubrantValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface IAddTransactionCarburantCommand {
    String message() default "Requête d'ajout de transaction carburant invalide";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
