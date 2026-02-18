package com.example.BacK.infrastructure.services.g_Formation;

import com.example.BacK.application.interfaces.g_Formation.Certificat.ICertificatRepositoryService;
import com.example.BacK.domain.g_Formation.Certificat;
import com.example.BacK.infrastructure.repository.g_Formation.CertificatRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;


@Service
public class CertificatRepositoryService implements ICertificatRepositoryService {

    private final CertificatRepository certificatRepository;

    public CertificatRepositoryService(CertificatRepository certificatRepository) {
        this.certificatRepository = certificatRepository;
    }

    @Override
    public Certificat saveCertificat(Certificat certificat) {
        return certificatRepository.save(certificat);
    }

    @Override
    public Certificat updateCertificat(Long id, Certificat certificat) {
        return certificatRepository.findById(id)
                .map(existing -> {
                    existing.setTitre(certificat.getTitre());
                    existing.setDescription(certificat.getDescription());
                    existing.setNiveau(certificat.getNiveau());
                    existing.setApprenant(certificat.getApprenant());
                    existing.setExamen(certificat.getExamen());
                    return certificatRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Certificat non trouvé avec l’ID : " + id));
    }

    @Override
    public void deleteCertificat(Long id) {
        if (!certificatRepository.existsById(id)) {
            throw new RuntimeException("Certificat non trouvé avec l’ID : " + id);
        }
        certificatRepository.deleteById(id);
    }

    @Override
    public Optional<Certificat> getCertificatById(Long id) {
        return certificatRepository.findById(id);
    }

    @Override
    public List<Certificat> getAllCertificats() {
        return certificatRepository.findAll();
    }

    @Override
    public List<Certificat> getCertificatsByExamen(Long idExamen) {
        return certificatRepository.findByExamen_IdExamen(idExamen);
    }

    @Override
    public boolean existsById(Long id) {
        return certificatRepository.existsById(id);
    }
}
