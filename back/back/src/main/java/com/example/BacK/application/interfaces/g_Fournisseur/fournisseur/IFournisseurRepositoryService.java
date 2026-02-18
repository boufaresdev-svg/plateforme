package com.example.BacK.application.interfaces.g_Fournisseur.fournisseur;

import com.example.BacK.application.g_Fournisseur.Query.fournisseur.GetFournisseurQuery;
import com.example.BacK.application.g_Fournisseur.Query.fournisseur.GetFournisseurResponse;
import com.example.BacK.application.models.PageResponse;
import com.example.BacK.domain.g_Fournisseur.Fournisseur;

import java.util.List;

public interface IFournisseurRepositoryService {
    
    String add(Fournisseur fournisseur);
    void update(Fournisseur fournisseur);
    void delete(String id);
    Fournisseur getById(String id);
    List<GetFournisseurResponse> getById(GetFournisseurQuery query);
    List<GetFournisseurResponse> search(GetFournisseurQuery query);
    List<GetFournisseurResponse> getAll();
    PageResponse<GetFournisseurResponse> getAllPaginated(GetFournisseurQuery query);
    PageResponse<GetFournisseurResponse> searchPaginated(GetFournisseurQuery query);
}