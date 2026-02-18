package com.example.BacK.application.interfaces.g_Formation.Certificat;


import com.example.BacK.domain.g_Formation.Certificat;

import java.util.List;
import java.util.Optional;

public interface ICertificatRepositoryService {

    Certificat saveCertificat(Certificat certificat);
    Certificat updateCertificat(Long id, Certificat certificat);
    void deleteCertificat(Long id);
    Optional<Certificat> getCertificatById(Long id);
    List<Certificat> getAllCertificats();
    List<Certificat> getCertificatsByExamen(Long idExamen);
    boolean existsById(Long id);



}
