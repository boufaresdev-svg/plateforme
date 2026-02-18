package com.example.BacK.application.g_Formation.Query.Certificat;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Formation.Certificat;
import com.example.BacK.infrastructure.services.g_Formation.CertificatRepositoryService;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;


@Component("GetCertificatHandler")
public class GetCertificatHandler implements RequestHandler<GetCertificatQuery, List<GetCertificatResponse>> {

    private final CertificatRepositoryService certificatRepositoryService;

    public GetCertificatHandler(CertificatRepositoryService certificatRepositoryService) {
        this.certificatRepositoryService = certificatRepositoryService;
    }

    @Override
    public List<GetCertificatResponse> handle(GetCertificatQuery command) {

        if (command.getIdCertificat() != null) {
            return certificatRepositoryService.getCertificatById(command.getIdCertificat())
                    .map(certificat -> List.of(mapToResponse(certificat)))
                    .orElse(List.of());
        }

        return certificatRepositoryService.getAllCertificats().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private GetCertificatResponse mapToResponse(Certificat certificat) {
        return new GetCertificatResponse(
                certificat.getIdCertificat(),
                certificat.getTitre(),
                certificat.getDescription(),
                certificat.getNiveau(),
                certificat.getApprenant() != null ? certificat.getApprenant().getId() : null,
                certificat.getExamen() != null ? certificat.getExamen().getIdExamen() : null
        );
    }
}
