package com.example.BacK.application.g_Formation.Query.Certificat.CertificatByExamen;

import com.example.BacK.application.models.g_formation.ApprenantDTO;
import com.example.BacK.application.models.g_formation.ExamenDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetCertificatByExamenResponse {

    private Long idCertificat;
    private String titre;
    private String description;
    private String niveau;
    private ApprenantDTO apprenant;
    private ExamenDTO examen;

}
