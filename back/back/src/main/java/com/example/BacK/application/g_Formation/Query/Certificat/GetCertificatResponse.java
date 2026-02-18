package com.example.BacK.application.g_Formation.Query.Certificat;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetCertificatResponse {

    private Long idCertificat;
    private String titre;
    private String description;
    private String niveau;
    private Long ApprenantId;
    private Long ExamenId;



}
