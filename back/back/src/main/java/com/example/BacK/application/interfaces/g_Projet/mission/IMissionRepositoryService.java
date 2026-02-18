package com.example.BacK.application.interfaces.g_Projet.mission;

import com.example.BacK.application.g_Projet.Query.mission.all.GetMissionResponse;
import com.example.BacK.domain.g_Projet.Mission;

import java.util.List;

public interface IMissionRepositoryService {
    String add(Mission mission );
    void update(Mission mission );
    void delete(String id);
    Mission  get(String id);
    List<GetMissionResponse> getall( );
}