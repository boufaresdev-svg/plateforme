package com.example.BacK.application.g_Formation.Command.Certificat.updateCertificat;

import com.example.BacK.application.interfaces.g_Formation.Certificat.IUpdateCertificatCommand;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UpdateCertificatValidator implements ConstraintValidator<IUpdateCertificatCommand, UpdateCertificatCommand> {
    @Override
    public boolean isValid(UpdateCertificatCommand updateCertificatCommand, ConstraintValidatorContext constraintValidatorContext) {
        return false;
    }
}
