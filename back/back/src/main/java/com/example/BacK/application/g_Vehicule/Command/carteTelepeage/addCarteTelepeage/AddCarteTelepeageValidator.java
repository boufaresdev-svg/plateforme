package com.example.BacK.application.g_Vehicule.Command.carteTelepeage.addCarteTelepeage;

  import com.example.BacK.application.interfaces.g_Vehicule.carteTelepeage.IAddCarteTelepeageCommand;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AddCarteTelepeageValidator implements ConstraintValidator<IAddCarteTelepeageCommand, AddCarteTelepeageCommand> {
    @Override
    public boolean isValid(AddCarteTelepeageCommand command, ConstraintValidatorContext constraintValidatorContext) {
        if (command == null) {
            return false;
        }
        return true;
    }
}