package com.example.BacK.infrastructure.services.g_Fournisseur;

import com.example.BacK.application.exceptions.FournisseurNameAlreadyExistsException;
import com.example.BacK.application.g_Fournisseur.Query.fournisseur.GetFournisseurQuery;
import com.example.BacK.application.g_Fournisseur.Query.fournisseur.GetFournisseurResponse;
import com.example.BacK.application.interfaces.g_Fournisseur.fournisseur.IFournisseurRepositoryService;
import com.example.BacK.application.models.PageResponse;
import com.example.BacK.domain.g_Fournisseur.DettesFournisseur;
import com.example.BacK.domain.g_Fournisseur.Fournisseur;
import com.example.BacK.domain.g_Fournisseur.TranchePaiement;
import com.example.BacK.infrastructure.repository.g_Fournisseur.FournisseurRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class FournisseurRepositoryService implements IFournisseurRepositoryService {

    private final FournisseurRepository fournisseurRepository;
    private final ModelMapper mapper;

    public FournisseurRepositoryService(FournisseurRepository fournisseurRepository, ModelMapper mapper) {
        this.fournisseurRepository = fournisseurRepository;
        this.mapper = mapper;
    }

    @Override
    public String add(Fournisseur fournisseur) {
        if (fournisseurRepository.existsByNom(fournisseur.getNom())) {
            throw FournisseurNameAlreadyExistsException.withName(fournisseur.getNom());
        }
        
        fournisseur.setId(null);
        Fournisseur savedFournisseur = fournisseurRepository.save(fournisseur);
        return savedFournisseur.getId();
    }

    @Override
    public void update(Fournisseur fournisseur) {
        if (!fournisseurRepository.existsById(fournisseur.getId())) {
            throw new IllegalArgumentException("Fournisseur ID not found: " + fournisseur.getId());
        }
        
        if (fournisseurRepository.existsByNomAndIdNot(fournisseur.getNom(), fournisseur.getId())) {
            throw FournisseurNameAlreadyExistsException.withName(fournisseur.getNom());
        }
        
        fournisseurRepository.save(fournisseur);
    }

    @Override
    public void delete(String id) {
        if (!fournisseurRepository.existsById(id)) {
            throw new IllegalArgumentException("Fournisseur ID not found: " + id);
        }
        fournisseurRepository.deleteById(id);
    }

    @Override
    public Fournisseur getById(String id) {
        Optional<Fournisseur> fournisseur = fournisseurRepository.findByIdWithDettes(id);
        return fournisseur.orElse(null);
    }

    @Override
    public List<GetFournisseurResponse> getById(GetFournisseurQuery query) {
        Optional<Fournisseur> fournisseur = fournisseurRepository.findByIdWithDettes(query.getId());
        if (fournisseur.isPresent()) {
            return Collections.singletonList(mapToResponse(fournisseur.get()));
        }
        return Collections.emptyList();
    }

    @Override
    public List<GetFournisseurResponse> search(GetFournisseurQuery query) {
        List<Fournisseur> fournisseurs = fournisseurRepository.findBySearchCriteria(
            query.getNom(),
            query.getInfoContact(),
            query.getAdresse(),
            query.getTelephone(),
            query.getMatriculeFiscale(),
            null
        );
        return fournisseurs.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<GetFournisseurResponse> getAll() {
        List<Fournisseur> fournisseurs = fournisseurRepository.findAllWithDettes();
        return fournisseurs.stream()
                .map(this::mapToResponse)
                .toList();
    }
    
    @Override
    public PageResponse<GetFournisseurResponse> getAllPaginated(GetFournisseurQuery query) {
        Pageable pageable = createPageable(query);
        Page<Fournisseur> page = fournisseurRepository.findAllPaginated(pageable);
        
        // Load dettes for each fournisseur in the page
        List<GetFournisseurResponse> content = page.getContent().stream()
                .map(f -> {
                    Fournisseur fournisseurWithDettes = fournisseurRepository.findByIdWithDettes(f.getId()).orElse(f);
                    return mapToResponse(fournisseurWithDettes);
                })
                .toList();
        
        return createPageResponse(content, page);
    }
    
    @Override
    public PageResponse<GetFournisseurResponse> searchPaginated(GetFournisseurQuery query) {
        Pageable pageable = createPageable(query);
        Page<Fournisseur> page = fournisseurRepository.findBySearchCriteriaPaginated(
            query.getNom(),
            query.getInfoContact(),
            query.getAdresse(),
            query.getTelephone(),
            query.getMatriculeFiscale(),
            null,
            pageable
        );
        
        // Load dettes for each fournisseur in the page
        List<GetFournisseurResponse> content = page.getContent().stream()
                .map(f -> {
                    Fournisseur fournisseurWithDettes = fournisseurRepository.findByIdWithDettes(f.getId()).orElse(f);
                    return mapToResponse(fournisseurWithDettes);
                })
                .toList();
        
        return createPageResponse(content, page);
    }
    
    private Pageable createPageable(GetFournisseurQuery query) {
        int page = query.getPage() != null ? query.getPage() : 0;
        int size = query.getSize() != null ? query.getSize() : 10;
        String sortBy = query.getSortBy() != null ? query.getSortBy() : "nom";
        String sortDirection = query.getSortDirection() != null ? query.getSortDirection() : "ASC";
        
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
    
    private GetFournisseurResponse mapToResponse(Fournisseur fournisseur) {
        GetFournisseurResponse response = mapper.map(fournisseur, GetFournisseurResponse.class);
        
        if (fournisseur.getDettes() != null) {
            response.setNombreDettes(fournisseur.getDettes().size());
            response.setTotalDettes(
                fournisseur.getDettes().stream()
                    .mapToDouble(dette -> dette.getMontantTotal() != null ? dette.getMontantTotal().doubleValue() : 0.0)
                    .sum()
            );
            
            response.setSoldeTotal(calculerSoldeTotal(fournisseur));
            response.setMontantAPayer(calculerMontantAPayer(fournisseur));
        } else {
            response.setNombreDettes(0);
            response.setTotalDettes(0.0);
            response.setSoldeTotal(0.0);
            response.setMontantAPayer(0.0);
        }
        
        return response;
    }
    
    private Double calculerSoldeTotal(Fournisseur fournisseur) {
        if (fournisseur.getDettes() == null || fournisseur.getDettes().isEmpty()) {
            return 0.0;
        }
        
        return fournisseur.getDettes().stream()
            .filter(dette -> dette.getMontantTotal() != null)
            .mapToDouble(dette -> dette.getMontantTotal().doubleValue())
            .sum();
    }
    
    private Double calculerMontantAPayer(Fournisseur fournisseur) {
        if (fournisseur.getDettes() == null || fournisseur.getDettes().isEmpty()) {
            return 0.0;
        }
        
        return fournisseur.getDettes().stream()
            .filter(dette -> !Boolean.TRUE.equals(dette.getEstPaye()))
            .mapToDouble(dette -> {
                Double soldeRestant = obtenirSoldeRestant(dette);
                return soldeRestant != null ? soldeRestant : 0.0;
            })
            .sum();
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
}