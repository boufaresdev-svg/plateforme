package com.example.BacK.application.g_Formation.Command.Certificat.deleteCertificat;


import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.infrastructure.services.g_Formation.CertificatRepositoryService;
import org.springframework.stereotype.Component;


@Component("DeleteCertificatHandler")
public class DeleteCertificatHandler implements RequestHandler<DeleteCertificatCommand, Void> {

    private final CertificatRepositoryService certificatRepositoryService;

    public DeleteCertificatHandler(CertificatRepositoryService certificatRepositoryService) {
        this.certificatRepositoryService = certificatRepositoryService;
    }

    @Override
    public Void handle(DeleteCertificatCommand command) {

        Long id = command.getIdCertificat();

        if (id == null) {
            throw new IllegalArgumentException("L’ID du certificat ne peut pas être nul.");
        }

        if (!certificatRepositoryService.existsById(id)) {
            throw new IllegalArgumentException("Aucun certificat trouvé avec l’ID : " + id);
        }

        certificatRepositoryService.deleteCertificat(id);

        return null;
    }
}
