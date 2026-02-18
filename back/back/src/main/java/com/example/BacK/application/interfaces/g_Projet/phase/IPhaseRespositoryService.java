package com.example.BacK.application.interfaces.g_Projet.phase;

import com.example.BacK.application.g_Projet.Query.phase.all.GetPhaseResponse;
import com.example.BacK.domain.g_Projet.Phase;

import java.util.List;

public interface IPhaseRespositoryService {
    String add(Phase phase );
    void update(Phase phase );
    void delete(String id);
    Phase  get(String id);
    List<GetPhaseResponse> getall( );
}