package com.example.BacK.application.models.g_formation;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CertificatDTO {

    private Long idCertificat;
    private String titre;
    private String description;
    private String niveau;
    private Long idApprenant;
    private Long idExamen;

}
