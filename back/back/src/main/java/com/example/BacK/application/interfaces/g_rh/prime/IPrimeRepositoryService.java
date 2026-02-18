package com.example.BacK.application.interfaces.g_rh.prime;

import com.example.BacK.application.g_RH.Query.prime.GetPrimeResponse;
import com.example.BacK.domain.g_RH.Prime;

import java.util.List;

public interface IPrimeRepositoryService {
    String add(Prime prime);
    void update(Prime prime);
    void delete(String id);
    Prime  get(String id);
    List<GetPrimeResponse> getall( );
}
