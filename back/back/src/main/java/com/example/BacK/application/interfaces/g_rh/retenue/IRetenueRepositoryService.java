package com.example.BacK.application.interfaces.g_rh.retenue;

import com.example.BacK.application.g_RH.Query.retenue.GetReteuneResponse;
import com.example.BacK.domain.g_RH.Retenue;

import java.util.List;


public interface IRetenueRepositoryService {
    String add(Retenue retenue);
    void update(Retenue retenue);
    void delete(String id);
    Retenue get(String id);
    List<GetReteuneResponse> getall();
}