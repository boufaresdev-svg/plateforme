package com.example.BacK.application.g_Formation.Command.Certificat.addCertificat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddCertificatCommand {

    private String titre;
    private String description;
    private String niveau;
    private Long idApprenant;
    private Long idExamen;


}
