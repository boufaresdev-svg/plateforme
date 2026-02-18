package com.example.BacK.application.models.g_vehicule;

import com.example.BacK.domain.g_Vehicule.enumEntity.TypeCarburant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionCarburantDTO {
    private String id;
    private LocalDate date;
    private String station;
    private String adresseStation;
    private Double quantite;    // litres
    private Double prixLitre;
    private Double montantTotal;
     private TypeCarburant typeCarburant;
    private String conducteur;
    private CarteGazoilDTO carte;
    private double consommation;
    private String carteTelepeageId;
    private Double montantTelepeage;
    private Double kilometrage;
}
