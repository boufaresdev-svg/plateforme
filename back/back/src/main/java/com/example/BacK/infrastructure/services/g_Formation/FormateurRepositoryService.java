package com.example.BacK.infrastructure.services.g_Formation;

import com.example.BacK.application.interfaces.g_Formation.Formateur.IFormateurRepositoryService;
import com.example.BacK.domain.g_Formation.Formateur;
import com.example.BacK.infrastructure.repository.g_Formation.FormateurRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FormateurRepositoryService implements IFormateurRepositoryService {

    private final FormateurRepository formateurRepository;

    public FormateurRepositoryService(FormateurRepository formateurRepository) {
        this.formateurRepository = formateurRepository;
    }

    @Override
    public Formateur saveFormateur(Formateur formateur) {
        return formateurRepository.save(formateur);
    }

    @Override
    public Formateur updateFormateur(Long id, Formateur formateur) {
        return formateurRepository.findById(id)
                .map(existing -> {
                    existing.setNom(formateur.getNom());
                    existing.setPrenom(formateur.getPrenom());
                    existing.setSpecialite(formateur.getSpecialite());
                    existing.setContact(formateur.getContact());
                    existing.setExperience(formateur.getExperience());
                    existing.setDocumentUrl(formateur.getDocumentUrl());
                    return formateurRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Formateur non trouvé avec l'id : " + id));
    }

    @Override
    public void deleteFormateur(Long id) {
        if (!formateurRepository.existsById(id)) {
            throw new RuntimeException("Formateur non trouvé avec l'id : " + id);
        }
        formateurRepository.deleteById(id);
    }

    @Override
    public Optional<Formateur> getFormateurById(Long id) {
        return formateurRepository.findById(id);
    }

    @Override
    public List<Formateur> getAllFormateurs() {
        return formateurRepository.findAll();
    }

    public boolean exists(Long idFormateur) {
        return formateurRepository.existsById(idFormateur);
    }

}
