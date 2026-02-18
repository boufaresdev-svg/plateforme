package com.example.BacK.infrastructure.services.g_Fournisseur;

import com.example.BacK.application.g_Fournisseur.Query.dettesFournisseur.GetDettesFournisseurQuery;
import com.example.BacK.application.g_Fournisseur.Query.dettesFournisseur.GetDettesFournisseurResponse;
import com.example.BacK.application.interfaces.g_Fournisseur.dettesFournisseur.IDettesFournisseurRepositoryService;
import com.example.BacK.application.models.PageResponse;
import com.example.BacK.domain.g_Fournisseur.DettesFournisseur;
import com.example.BacK.domain.g_Fournisseur.TranchePaiement;
import com.example.BacK.infrastructure.repository.g_Fournisseur.DettesFournisseurRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class DettesFournisseurRepositoryService implements IDettesFournisseurRepositoryService {

    private final DettesFournisseurRepository dettesFournisseurRepository;
    private final ModelMapper mapper;

    public DettesFournisseurRepositoryService(DettesFournisseurRepository dettesFournisseurRepository, ModelMapper mapper) {
        this.dettesFournisseurRepository = dettesFournisseurRepository;
        this.mapper = mapper;
    }

    @Override
    public String add(DettesFournisseur dettesFournisseur) {
        dettesFournisseur.setId(null);
        DettesFournisseur savedDette = dettesFournisseurRepository.save(dettesFournisseur);
        return savedDette.getId();
    }

    @Override
    public void update(DettesFournisseur dettesFournisseur) {
        if (!dettesFournisseurRepository.existsById(dettesFournisseur.getId())) {
            throw new IllegalArgumentException("DettesFournisseur ID not found: " + dettesFournisseur.getId());
        }
        dettesFournisseurRepository.save(dettesFournisseur);
    }

    @Override
    public void delete(String id) {
        if (!dettesFournisseurRepository.existsById(id)) {
            throw new IllegalArgumentException("DettesFournisseur ID not found: " + id);
        }
        dettesFournisseurRepository.deleteById(id);
    }

    @Override
    public DettesFournisseur getById(String id) {
        return dettesFournisseurRepository.findByIdWithFournisseur(id).orElse(null);
    }

    @Override
    public List<GetDettesFournisseurResponse> getAll() {
        List<DettesFournisseur> dettes = dettesFournisseurRepository.findAllWithFournisseur();
        return dettes.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<GetDettesFournisseurResponse> getByFournisseurId(String fournisseurId) {
        List<DettesFournisseur> dettes = dettesFournisseurRepository.findByFournisseurId(fournisseurId);
        return dettes.stream()
                .map(this::mapToResponse)
                .toList();
    }
    
    @Override
    public List<GetDettesFournisseurResponse> search(GetDettesFournisseurQuery query) {
        List<DettesFournisseur> dettes = dettesFournisseurRepository.findBySearchCriteria(
            query.getNumeroFacture(),
            query.getTitre(),
            query.getEstPaye(),
            query.getDatePaiementPrevue(),
            query.getDatePaiementReelle(),
            query.getFournisseurId(),
            query.getFournisseurNom()
        );
        return dettes.stream()
                .map(this::mapToResponse)
                .toList();
    }
    
    @Override
    public boolean existsByNumeroFacture(String numeroFacture) {
        return dettesFournisseurRepository.existsByNumeroFacture(numeroFacture);
    }
    
    @Override
    public PageResponse<GetDettesFournisseurResponse> getAllPaginated(GetDettesFournisseurQuery query) {
        Pageable pageable = createPageable(query);
        Page<DettesFournisseur> page = dettesFournisseurRepository.findAllPaginated(pageable);
        
        // Load fournisseur for each dette in the page
        List<GetDettesFournisseurResponse> content = page.getContent().stream()
                .map(d -> {
                    DettesFournisseur detteWithFournisseur = dettesFournisseurRepository.findByIdWithFournisseur(d.getId()).orElse(d);
                    return mapToResponse(detteWithFournisseur);
                })
                .toList();
        
        return createPageResponse(content, page);
    }
    
    @Override
    public PageResponse<GetDettesFournisseurResponse> getByFournisseurIdPaginated(GetDettesFournisseurQuery query) {
        Pageable pageable = createPageable(query);
        Page<DettesFournisseur> page = dettesFournisseurRepository.findByFournisseurIdPaginated(
            query.getFournisseurId(), 
            pageable
        );
        
        // Load fournisseur for each dette in the page
        List<GetDettesFournisseurResponse> content = page.getContent().stream()
                .map(d -> {
                    DettesFournisseur detteWithFournisseur = dettesFournisseurRepository.findByIdWithFournisseur(d.getId()).orElse(d);
                    return mapToResponse(detteWithFournisseur);
                })
                .toList();
        
        return createPageResponse(content, page);
    }
    
    @Override
    public PageResponse<GetDettesFournisseurResponse> searchPaginated(GetDettesFournisseurQuery query) {
        Pageable pageable = createPageable(query);
        Page<DettesFournisseur> page = dettesFournisseurRepository.findBySearchCriteriaPaginated(
            query.getNumeroFacture(),
            query.getTitre(),
            query.getEstPaye(),
            query.getDatePaiementPrevue(),
            query.getDatePaiementReelle(),
            query.getFournisseurId(),
            query.getFournisseurNom(),
            pageable
        );
        
        // Load fournisseur for each dette in the page
        List<GetDettesFournisseurResponse> content = page.getContent().stream()
                .map(d -> {
                    DettesFournisseur detteWithFournisseur = dettesFournisseurRepository.findByIdWithFournisseur(d.getId()).orElse(d);
                    return mapToResponse(detteWithFournisseur);
                })
                .toList();
        
        return createPageResponse(content, page);
    }
    
    @Override
    public List<GetDettesFournisseurResponse> flexibleSearch(String searchTerm, Boolean estPaye, String fournisseurId, LocalDate dateDebut, LocalDate dateFin) {
        List<DettesFournisseur> dettes = dettesFournisseurRepository.findByFlexibleSearch(
            searchTerm, estPaye, fournisseurId, dateDebut, dateFin
        );
        return dettes.stream()
                .map(this::mapToResponse)
                .toList();
    }
    
    @Override
    public PageResponse<GetDettesFournisseurResponse> flexibleSearchPaginated(String searchTerm, Boolean estPaye, String fournisseurId, LocalDate dateDebut, LocalDate dateFin, GetDettesFournisseurQuery query) {
        Pageable pageable = createPageable(query);
        Page<DettesFournisseur> page = dettesFournisseurRepository.findByFlexibleSearchPaginated(
            searchTerm, estPaye, fournisseurId, dateDebut, dateFin, pageable
        );
        
        // Load fournisseur for each dette in the page
        List<GetDettesFournisseurResponse> content = page.getContent().stream()
                .map(d -> {
                    DettesFournisseur detteWithFournisseur = dettesFournisseurRepository.findByIdWithFournisseur(d.getId()).orElse(d);
                    return mapToResponse(detteWithFournisseur);
                })
                .toList();
        
        return createPageResponse(content, page);
    }
    
    private Pageable createPageable(GetDettesFournisseurQuery query) {
        int page = query.getPage() != null ? query.getPage() : 0;
        int size = query.getSize() != null ? query.getSize() : 10;
        String sortBy = query.getSortBy() != null ? query.getSortBy() : "createdAt";
        String sortDirection = query.getSortDirection() != null ? query.getSortDirection() : "DESC";
        
        Sort sort = sortDirection.equalsIgnoreCase("DESC") ? 
                    Sort.by(sortBy).descending() : 
                    Sort.by(sortBy).ascending();
        
        return PageRequest.of(page, size, sort);
    }
    
    private <T> PageResponse<T> createPageResponse(List<T> content, Page<?> page) {
        PageResponse<T> response = new PageResponse<>();
        response.setContent(content);
        response.setPageNumber(page.getNumber());
        response.setPageSize(page.getSize());
        response.setTotalElements(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        response.setFirst(page.isFirst());
        response.setLast(page.isLast());
        response.setEmpty(page.isEmpty());
        return response;
    }
    
    private GetDettesFournisseurResponse mapToResponse(DettesFournisseur dette) {
        GetDettesFournisseurResponse response = mapper.map(dette, GetDettesFournisseurResponse.class);
        
        if (dette.getFournisseur() != null) {
            response.setFournisseurId(dette.getFournisseur().getId());
            response.setFournisseurNom(dette.getFournisseur().getNom());
        }
        
        Double soldeRestant = obtenirSoldeRestant(dette);
        response.setSoldeRestant(soldeRestant);
        response.setMontantDu(soldeRestant);  // Map soldeRestant to montantDu for frontend compatibility
        response.setEnRetard(estDetteEnRetard(dette));
        
        return response;
    }
    
    private Double obtenirSoldeRestant(DettesFournisseur dette) {
        if (Boolean.TRUE.equals(dette.getEstPaye())) {
            return 0.0;
        }

        if (dette.getMontantTotal() == null) {
            return 0.0;
        }

        double montantPaye = dette.getTranchesPaiement().stream()
            .filter(tranche -> Boolean.TRUE.equals(tranche.getEstPaye()))
            .mapToDouble(TranchePaiement::getMontant)
            .sum();
        
        double soldeRestant = dette.getMontantTotal() - montantPaye;
        return Math.max(0.0, soldeRestant);
    }
    
    private boolean estDetteEnRetard(DettesFournisseur dette) {
        if (Boolean.TRUE.equals(dette.getEstPaye())) {
            return false;
        }
        
        return dette.getTranchesPaiement().stream()
            .anyMatch(this::estTrancheEnRetard);
    }
    
    private boolean estTrancheEnRetard(TranchePaiement tranche) {
        if (Boolean.TRUE.equals(tranche.getEstPaye())) {
            return false;
        }
        return tranche.getDateEcheance() != null && LocalDate.now().isAfter(tranche.getDateEcheance());
    }
}