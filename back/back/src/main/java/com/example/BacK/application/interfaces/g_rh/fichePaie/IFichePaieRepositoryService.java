package com.example.BacK.application.interfaces.g_rh.fichePaie;

import com.example.BacK.application.g_RH.Query.fichePaie.GetFichePaieResponse;
import com.example.BacK.domain.g_RH.FichePaie;

import java.util.List;

public interface IFichePaieRepositoryService {
    String add(FichePaie fichePaie);
    void update(FichePaie fichePaie);
    void delete(String id);
    FichePaie get(String id);
    List<GetFichePaieResponse> getall( );
}