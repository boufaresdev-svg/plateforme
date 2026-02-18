package com.example.BacK.application.interfaces.g_Projet.tache;

import com.example.BacK.application.g_Projet.Query.Tache.all.GetTacheResponse;
import com.example.BacK.domain.g_Projet.Tache;

import java.util.List;

public interface ITacheRepositoryService {
    String add(Tache tache );
    void update(Tache tache  );
    void delete(String id);
    Tache  get(String id);
    List<GetTacheResponse> getall( );
}