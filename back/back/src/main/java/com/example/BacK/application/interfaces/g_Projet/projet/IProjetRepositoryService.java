package com.example.BacK.application.interfaces.g_Projet.projet;

import com.example.BacK.application.g_Projet.Query.projet.all.GetProjetResponse;
import com.example.BacK.domain.g_Projet.Projet;
import java.util.List;

public interface IProjetRepositoryService {
    String add(Projet projet );
    void update(Projet projet );
    void delete(String id);
    Projet  get(String id);
    List<GetProjetResponse> getall( );
}