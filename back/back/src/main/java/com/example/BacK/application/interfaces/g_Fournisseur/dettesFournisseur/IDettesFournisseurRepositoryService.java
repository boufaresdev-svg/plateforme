package com.example.BacK.application.interfaces.g_Fournisseur.dettesFournisseur;

import com.example.BacK.application.g_Fournisseur.Query.dettesFournisseur.GetDettesFournisseurQuery;
import com.example.BacK.application.g_Fournisseur.Query.dettesFournisseur.GetDettesFournisseurResponse;
import com.example.BacK.application.models.PageResponse;
import com.example.BacK.domain.g_Fournisseur.DettesFournisseur;

import java.time.LocalDate;
import java.util.List;

public interface IDettesFournisseurRepositoryService {
    
    String add(DettesFournisseur dettesFournisseur);
    void update(DettesFournisseur dettesFournisseur);
    void delete(String id);
    DettesFournisseur getById(String id);
    List<GetDettesFournisseurResponse> getAll();
    List<GetDettesFournisseurResponse> getByFournisseurId(String fournisseurId);
    List<GetDettesFournisseurResponse> search(GetDettesFournisseurQuery query);
    boolean existsByNumeroFacture(String numeroFacture);
    PageResponse<GetDettesFournisseurResponse> getAllPaginated(GetDettesFournisseurQuery query);
    PageResponse<GetDettesFournisseurResponse> getByFournisseurIdPaginated(GetDettesFournisseurQuery query);
    PageResponse<GetDettesFournisseurResponse> searchPaginated(GetDettesFournisseurQuery query);
    
    // New flexible search methods
    List<GetDettesFournisseurResponse> flexibleSearch(String searchTerm, Boolean estPaye, String fournisseurId, LocalDate dateDebut, LocalDate dateFin);
    PageResponse<GetDettesFournisseurResponse> flexibleSearchPaginated(String searchTerm, Boolean estPaye, String fournisseurId, LocalDate dateDebut, LocalDate dateFin, GetDettesFournisseurQuery query);
}