package com.example.BacK.application.g_Formation.Command.Certificat.updateCertificat;


import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Formation.Apprenant;
import com.example.BacK.domain.g_Formation.Certificat;
import com.example.BacK.domain.g_Formation.Examen;
import com.example.BacK.infrastructure.services.g_Formation.ApprenantRepositoryService;
import com.example.BacK.infrastructure.services.g_Formation.CertificatRepositoryService;
import com.example.BacK.infrastructure.services.g_Formation.ExamenRepositoryService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;


@Component("UpdateCertificatHandler")
public class UpdateCertificatHandler implements RequestHandler<UpdateCertificatCommand, Void> {

    private final CertificatRepositoryService certificatRepositoryService;
    private final ApprenantRepositoryService apprenantRepositoryService;
    private final ExamenRepositoryService examenRepositoryService;
    private final ModelMapper modelMapper;

    public UpdateCertificatHandler(CertificatRepositoryService certificatRepositoryService,
                                   ApprenantRepositoryService apprenantRepositoryService,
                                   ExamenRepositoryService examenRepositoryService,
                                   ModelMapper modelMapper) {
        this.certificatRepositoryService = certificatRepositoryService;
        this.apprenantRepositoryService = apprenantRepositoryService;
        this.examenRepositoryService = examenRepositoryService;
        this.modelMapper = modelMapper;
    }

    @Override
    public Void handle(UpdateCertificatCommand command) {
        Long id = command.getIdCertificat();

        if (id == null) {
            throw new IllegalArgumentException("L’ID du certificat ne peut pas être nul.");
        }

        Certificat certificat = certificatRepositoryService
                .getCertificatById(id)
                .orElseThrow(() -> new IllegalArgumentException("Aucun certificat trouvé avec l’ID : " + id));

        certificat.setTitre(command.getTitre());
        certificat.setDescription(command.getDescription());
        certificat.setNiveau(command.getNiveau());

        if (command.getIdApprenant() != null) {
            Apprenant apprenant = apprenantRepositoryService
                    .getApprenantById(command.getIdApprenant())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Aucun apprenant trouvé avec l’ID : " + command.getIdApprenant()
                    ));
            certificat.setApprenant(apprenant);
        }

        if (command.getIdExamen() != null) {
            Examen examen = examenRepositoryService
                    .getExamenById(command.getIdExamen())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Aucun examen trouvé avec l’ID : " + command.getIdExamen()
                    ));
            certificat.setExamen(examen);
        }

        certificatRepositoryService.updateCertificat(id, certificat);

        return null;
    }
}
