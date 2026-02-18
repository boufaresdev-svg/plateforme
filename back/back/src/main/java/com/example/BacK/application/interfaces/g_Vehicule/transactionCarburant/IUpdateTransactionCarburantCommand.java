package com.example.BacK.application.interfaces.g_Vehicule.transactionCarburant;
import com.example.BacK.application.g_Vehicule.Command.transactionCarburant.updateTransaction.UpdateTransactionCarburantValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UpdateTransactionCarburantValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface IUpdateTransactionCarburantCommand {
    String message() default "Requête de mise à jour de transaction carburant invalide";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
