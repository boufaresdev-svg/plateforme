package com.example.BacK.application.interfaces.g_Vehicule.carteGazole;

import com.example.BacK.application.g_Vehicule.Query.CarteGazoil.GetCarteGazoilResponse;
import com.example.BacK.domain.g_Vehicule.CarteGazoil;

import java.util.List;

public interface ICarteGazoilRepositoryService {
    String add(CarteGazoil carte);
    void update(CarteGazoil carte);
    void delete(String id);
    CarteGazoil get(String id);
    List<GetCarteGazoilResponse> getall( );
    void mise_a_jourSolde(CarteGazoil carteGazoil , double montant);
    void resete_a_jourSolde(CarteGazoil carteGazoil , double montant);
}