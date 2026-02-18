package com.example.BacK.application.interfaces.g_rh.congee;

import com.example.BacK.application.g_RH.Query.congee.GetCongeeResponse;
import com.example.BacK.domain.g_RH.Congee;

import java.util.List;

public interface ICongeeRepositoryService {
    String add(Congee congee);
    void update(Congee congee);
    void delete(String id);
    Congee get(String id);
    List<GetCongeeResponse> getall( );

}