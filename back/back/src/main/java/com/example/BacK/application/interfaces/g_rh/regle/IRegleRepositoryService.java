package com.example.BacK.application.interfaces.g_rh.regle;

import com.example.BacK.application.g_RH.Query.regle.GetRegleResponse;
import com.example.BacK.domain.g_RH.Regle;

import java.util.List;

public interface IRegleRepositoryService {
    String add(Regle regle);
    void update(Regle regle);
    void delete(String id);
    Regle   get(String id);
    List<GetRegleResponse> getall( );
}
