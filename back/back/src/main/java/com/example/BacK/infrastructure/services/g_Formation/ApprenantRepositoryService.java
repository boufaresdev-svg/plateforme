package com.example.BacK.infrastructure.services.g_Formation;

import com.example.BacK.application.interfaces.g_Formation.Apprenant.IApprenantRepositoryService;
import com.example.BacK.domain.g_Formation.Apprenant;
import com.example.BacK.domain.g_Formation.PlanFormation;
import com.example.BacK.infrastructure.repository.g_Formation.ApprenantRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class ApprenantRepositoryService implements IApprenantRepositoryService {

    private final ApprenantRepository apprenantRepository;
    private final PasswordEncoder passwordEncoder;

    public ApprenantRepositoryService(ApprenantRepository apprenantRepository, PasswordEncoder passwordEncoder) {
        this.apprenantRepository = apprenantRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Apprenant saveApprenant(Apprenant apprenant) {
        if (apprenant.getId() != null && !apprenantRepository.existsById(apprenant.getId())) {
            apprenant.setId(null);
        }
        // Generate matricule if not provided
        if (apprenant.getMatricule() == null || apprenant.getMatricule().isEmpty()) {
            apprenant.setMatricule(generateMatricule());
        }
        // Encode password if provided
        if (apprenant.getPassword() != null && !apprenant.getPassword().isEmpty()) {
            apprenant.setPassword(passwordEncoder.encode(apprenant.getPassword()));
        }
        return apprenantRepository.save(apprenant);
    }

    @Override
    public Apprenant updateApprenant(Long id, Apprenant apprenant) {
        return apprenantRepository.findById(id)
                .map(existing -> {
                    existing.setNom(apprenant.getNom());
                    existing.setPrenom(apprenant.getPrenom());
                    existing.setAdresse(apprenant.getAdresse());
                    existing.setTelephone(apprenant.getTelephone());
                    existing.setEmail(apprenant.getEmail());
                    existing.setPrerequis(apprenant.getPrerequis());
                    existing.setStatusInscription(apprenant.getStatusInscription());
                    existing.setPlanFormation(apprenant.getPlanFormation());
                    existing.setIsBlocked(apprenant.getIsBlocked());
                    existing.setIsStaff(apprenant.getIsStaff());
                    existing.setIsActive(apprenant.getIsActive());
                    // Only update password if provided and not empty
                    if (apprenant.getPassword() != null && !apprenant.getPassword().isEmpty()) {
                        existing.setPassword(passwordEncoder.encode(apprenant.getPassword()));
                    }
                    return apprenantRepository.save(existing);
                })
                .orElseThrow(() ->
                        new RuntimeException("Apprenant non trouvé avec l'ID : " + id));
    }

    @Override
    public void deleteApprenant(Long id) {
        if (!apprenantRepository.existsById(id)) {
            throw new RuntimeException("Apprenant non trouvé avec l'ID : " + id);
        }
        apprenantRepository.deleteById(id);
    }

    @Override
    public Optional<Apprenant> getApprenantById(Long id) {
        return apprenantRepository.findById(id);
    }

    @Override
    public List<Apprenant> getAllApprenants() {
        return apprenantRepository.findAll();
    }

    @Override
    public List<Apprenant> findByPlanFormationId(Long planFormationId) {
        return apprenantRepository.findByPlanFormation_IdPlanFormation(planFormationId);
    }

    @Override
    public boolean existsById(Long id) {
        return apprenantRepository.existsById(id);
    }
    
    // New methods
    
    public Page<Apprenant> searchApprenants(String search, int page, int size, String sortBy, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return apprenantRepository.searchApprenants(search, pageable);
    }
    
    public Optional<Apprenant> findByEmail(String email) {
        return apprenantRepository.findByEmail(email);
    }
    
    public Optional<Apprenant> findByMatricule(String matricule) {
        return apprenantRepository.findByMatricule(matricule);
    }
    
    public boolean existsByEmail(String email) {
        return apprenantRepository.existsByEmail(email);
    }
    
    public boolean existsByMatricule(String matricule) {
        return apprenantRepository.existsByMatricule(matricule);
    }
    
    public List<Apprenant> findByFormationId(Long formationId) {
        return apprenantRepository.findByFormationId(formationId);
    }
    
    public Page<Apprenant> findByFormationIdPaged(Long formationId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return apprenantRepository.findByFormationIdPaged(formationId, pageable);
    }
    
    public Apprenant blockApprenant(Long id) {
        return apprenantRepository.findById(id)
                .map(apprenant -> {
                    apprenant.setIsBlocked(true);
                    apprenant.setIsActive(false);
                    return apprenantRepository.save(apprenant);
                })
                .orElseThrow(() -> new RuntimeException("Apprenant non trouvé avec l'ID : " + id));
    }
    
    public Apprenant unblockApprenant(Long id) {
        return apprenantRepository.findById(id)
                .map(apprenant -> {
                    apprenant.setIsBlocked(false);
                    apprenant.setIsActive(true);
                    return apprenantRepository.save(apprenant);
                })
                .orElseThrow(() -> new RuntimeException("Apprenant non trouvé avec l'ID : " + id));
    }
    
    public Apprenant toggleStaff(Long id) {
        return apprenantRepository.findById(id)
                .map(apprenant -> {
                    apprenant.setIsStaff(!apprenant.getIsStaff());
                    return apprenantRepository.save(apprenant);
                })
                .orElseThrow(() -> new RuntimeException("Apprenant non trouvé avec l'ID : " + id));
    }
    
    public Apprenant updatePassword(Long id, String newPassword) {
        return apprenantRepository.findById(id)
                .map(apprenant -> {
                    apprenant.setPassword(passwordEncoder.encode(newPassword));
                    return apprenantRepository.save(apprenant);
                })
                .orElseThrow(() -> new RuntimeException("Apprenant non trouvé avec l'ID : " + id));
    }
    
    public String generatePassword() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
    
    public String generateMatricule() {
        String prefix = "APR";
        String timestamp = String.valueOf(System.currentTimeMillis()).substring(6);
        String random = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return prefix + timestamp + random;
    }
    
    public List<Apprenant> saveAll(List<Apprenant> apprenants) {
        for (Apprenant apprenant : apprenants) {
            if (apprenant.getMatricule() == null || apprenant.getMatricule().isEmpty()) {
                apprenant.setMatricule(generateMatricule());
            }
            if (apprenant.getPassword() != null && !apprenant.getPassword().isEmpty()) {
                apprenant.setPassword(passwordEncoder.encode(apprenant.getPassword()));
            }
        }
        return apprenantRepository.saveAll(apprenants);
    }
    
    public long countActiveApprenants() {
        return apprenantRepository.countActiveApprenants();
    }
    
    public long countBlockedApprenants() {
        return apprenantRepository.countBlockedApprenants();
    }
    
    public long countTotalApprenants() {
        return apprenantRepository.count();
    }
}


