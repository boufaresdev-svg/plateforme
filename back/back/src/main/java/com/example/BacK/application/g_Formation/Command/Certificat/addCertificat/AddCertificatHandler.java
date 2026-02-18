package com.example.BacK.application.g_Formation.Command.Certificat.addCertificat;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Formation.Apprenant;
import com.example.BacK.domain.g_Formation.Certificat;
import com.example.BacK.domain.g_Formation.Examen;
import com.example.BacK.infrastructure.services.g_Formation.ApprenantRepositoryService;
import com.example.BacK.infrastructure.services.g_Formation.CertificatRepositoryService;
import com.example.BacK.infrastructure.services.g_Formation.ExamenRepositoryService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;


@Component("AddCertificatHandler")
public class AddCertificatHandler implements RequestHandler<AddCertificatCommand, AddCertificatResponse> {

    private final CertificatRepositoryService certificatRepositoryService;
    private final ExamenRepositoryService examenRepositoryService;
    private final ModelMapper modelMapper;

    public AddCertificatHandler(CertificatRepositoryService certificatRepositoryService,
                                ExamenRepositoryService examenRepositoryService,
                                ModelMapper modelMapper) {
        this.certificatRepositoryService = certificatRepositoryService;
        this.examenRepositoryService = examenRepositoryService;
        this.modelMapper = modelMapper;
    }

    @Override
    public AddCertificatResponse handle(AddCertificatCommand command) {

        if (command.getIdExamen() == null) {
            throw new IllegalArgumentException("L'ID de l'examen est obligatoire !");
        }

        Examen examen = examenRepositoryService
                .getExamenById(command.getIdExamen())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Aucun examen trouvé avec l'ID : " + command.getIdExamen()));

        if (examen.getApprenant() == null) {
            throw new IllegalStateException("L'examen n'est associé à aucun apprenant !");
        }

        Certificat certificat = new Certificat();
        certificat.setTitre(command.getTitre());
        certificat.setDescription(command.getDescription());
        certificat.setNiveau(command.getNiveau());

        certificat.setExamen(examen);
        certificat.setApprenant(examen.getApprenant());

        Certificat saved = certificatRepositoryService.saveCertificat(certificat);

        return new AddCertificatResponse(
                saved.getIdCertificat(),
                "Certificat ajouté avec succès !");
    }
}
