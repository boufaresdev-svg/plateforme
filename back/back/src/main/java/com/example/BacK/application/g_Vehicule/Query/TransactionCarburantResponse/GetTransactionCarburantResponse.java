package com.example.BacK.application.g_Vehicule.Query.TransactionCarburantResponse;

import com.example.BacK.application.models.g_vehicule.CarteGazoilDTO;
import com.example.BacK.application.models.g_vehicule.VehiculeDTO;
import com.example.BacK.domain.g_Vehicule.enumEntity.TypeCarburant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GetTransactionCarburantResponse {
    private String id ;
    private LocalDate date;
    private String station;
    private Double quantite;
    private Double prixLitre;
    private Double montantTotal;
     private TypeCarburant typeCarburant;
    private String conducteur;
    private CarteGazoilDTO carte;
    private VehiculeDTO vehicule;
    private double consommation;
    private String carteTelepeageId;
    private Double montantTelepeage;
    private Double kilometrage;
    private Double ancienkilometrage;

}
