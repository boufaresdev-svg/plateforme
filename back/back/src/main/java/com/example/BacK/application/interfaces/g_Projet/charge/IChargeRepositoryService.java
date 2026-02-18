package com.example.BacK.application.interfaces.g_Projet.charge;

import com.example.BacK.application.g_Projet.Query.charge.GetChargeResponse;
import com.example.BacK.domain.g_Projet.Charge;
import java.util.List;

public interface IChargeRepositoryService {
    String add(Charge charge  );
    void update(Charge charge);
    void delete(String id);
    Charge get(String id);
    List<GetChargeResponse> getall( );

}