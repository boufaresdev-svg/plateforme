package com.example.BacK.application.g_Formation.Command.Certificat.addCertificat;

import com.example.BacK.application.interfaces.g_Formation.Certificat.IAddCertificatCommand;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AddCertificatValidator  implements ConstraintValidator<IAddCertificatCommand, AddCertificatCommand> {

    @Override
    public boolean isValid(AddCertificatCommand addCertificatCommand, ConstraintValidatorContext constraintValidatorContext) {
        return false;
    }
}
