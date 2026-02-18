package com.example.BacK.application.interfaces.g_Vehicule.Reparation;

import com.example.BacK.application.g_Vehicule.Query.Reparation.GetReparationResponse;
import com.example.BacK.domain.g_Vehicule.Reparation;

import java.util.List;

public interface IReparationRepositoryService {
    String add(Reparation reparation);
    void update(Reparation reparation);
    void delete(String id);
    Reparation get(String id);
    List<GetReparationResponse> filtre(Reparation filter);
}