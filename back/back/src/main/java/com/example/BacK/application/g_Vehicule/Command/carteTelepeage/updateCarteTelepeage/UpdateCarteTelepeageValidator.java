package com.example.BacK.application.g_Vehicule.Command.carteTelepeage.updateCarteTelepeage;
 
import com.example.BacK.application.interfaces.g_Vehicule.carteTelepeage.IUpdateCarteTelepeageCommand;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


public class UpdateCarteTelepeageValidator implements ConstraintValidator<IUpdateCarteTelepeageCommand, UpdateCarteTelepeageCommand> {
    @Override
    public boolean isValid(UpdateCarteTelepeageCommand command, ConstraintValidatorContext constraintValidatorContext) {
        if (command == null) {
            return false;
        }
        return true;
    }
}