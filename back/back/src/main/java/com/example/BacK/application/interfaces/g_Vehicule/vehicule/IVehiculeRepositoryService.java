package com.example.BacK.application.interfaces.g_Vehicule.vehicule;

import com.example.BacK.application.g_Vehicule.Query.vehicule.GetVehiculeResponse;
import com.example.BacK.domain.g_Vehicule.Vehicule;


import java.util.List;

public interface IVehiculeRepositoryService {
    String add(Vehicule vehicule);
    void update (Vehicule vehicule);
    void delete (String id);
    Vehicule get(String id);
    List<GetVehiculeResponse> getAll ( );
    void mise_a_jour_km ( Vehicule vehicule , double KM);

}
