package com.example.BacK.application.g_Formation.Query.Certificat.CertificatByExamen;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.application.models.g_formation.ApprenantDTO;
import com.example.BacK.application.models.g_formation.ExamenDTO;
import com.example.BacK.domain.g_Formation.Apprenant;
import com.example.BacK.domain.g_Formation.Certificat;
import com.example.BacK.infrastructure.services.g_Formation.CertificatRepositoryService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component("GetCertificatByExamenHandler")
public class GetCertificatByExamenHandler implements RequestHandler<GetCertificatByExamenQuery, List<GetCertificatByExamenResponse>> {

    private final CertificatRepositoryService certificatRepositoryService;
    private final ModelMapper modelMapper;

    public GetCertificatByExamenHandler(CertificatRepositoryService certificatRepositoryService,
                                        ModelMapper modelMapper) {
        this.certificatRepositoryService = certificatRepositoryService;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<GetCertificatByExamenResponse> handle(GetCertificatByExamenQuery query) {

        if (query.getIdExamen() == null) {
            throw new IllegalArgumentException("idExamen est obligatoire");
        }

        List<Certificat> certificats =
                certificatRepositoryService.getCertificatsByExamen(query.getIdExamen());

        return certificats.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private GetCertificatByExamenResponse mapToResponse(Certificat cert) {

        GetCertificatByExamenResponse dto = new GetCertificatByExamenResponse();

        dto.setIdCertificat(cert.getIdCertificat());
        dto.setTitre(cert.getTitre());
        dto.setDescription(cert.getDescription());
        dto.setNiveau(cert.getNiveau());

        if (cert.getExamen() != null) {
            dto.setExamen(modelMapper.map(cert.getExamen(), ExamenDTO.class));
        }

        if (cert.getExamen() != null && cert.getExamen().getApprenant() != null) {

            Apprenant apprenant = cert.getExamen().getApprenant();

            ApprenantDTO apDTO = new ApprenantDTO();
            apDTO.setIdApprenant(apprenant.getId());
            apDTO.setNom(apprenant.getNom());
            apDTO.setPrenom(apprenant.getPrenom());
            apDTO.setAdresse(apprenant.getAdresse());
            apDTO.setTelephone(apprenant.getTelephone());
            apDTO.setEmail(apprenant.getEmail());
            apDTO.setPrerequis(apprenant.getPrerequis());
            apDTO.setStatusInscription(apprenant.getStatusInscription());

            if (apprenant.getPlanFormation() != null) {
                apDTO.setIdPlanFormation(apprenant.getPlanFormation().getIdPlanFormation());
                apDTO.setTitrePlanFormation(apprenant.getPlanFormation().getTitre());
            }

            dto.setApprenant(apDTO);
        }

        return dto;
    }
}
