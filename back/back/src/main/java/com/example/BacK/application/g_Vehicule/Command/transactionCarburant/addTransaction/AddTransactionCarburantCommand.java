package com.example.BacK.application.g_Vehicule.Command.transactionCarburant.addTransaction;

import com.example.BacK.domain.g_Vehicule.enumEntity.TypeCarburant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddTransactionCarburantCommand {
    private LocalDate date;
    private String station;
    private Double quantite;
    private Double prixLitre;
    private Double montantTotal;
    private TypeCarburant typeCarburant;
    private String conducteur;
    private String vehiculeId;
    private String carteId;
    private double consommation;
    private String carteTelepeageId;
    private Double montantTelepeage;
    private Double kilometrage;
    private Double ancienkilometrage;


}
