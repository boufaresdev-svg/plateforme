package com.example.BacK.application.interfaces.g_Projet.LivrableTache;

import com.example.BacK.application.g_Projet.Query.EmployeAffecte.all.GetEmployeAffecteResponse;
import com.example.BacK.application.g_Projet.Query.LivrableTache.GetLivrableTacheQuery;
import com.example.BacK.application.g_Projet.Query.LivrableTache.GetLivrableTacheResponse;
import com.example.BacK.domain.g_Projet.EmployeAffecte;
import com.example.BacK.domain.g_Projet.LivrableTache;

import java.util.List;

public interface ILivrableTacheRepositoryService {
    String add(LivrableTache livrableTache );
    void update(LivrableTache livrableTache );
    void delete(String id);
    GetLivrableTacheResponse get(String id);
    List<GetLivrableTacheResponse> getall( );

}