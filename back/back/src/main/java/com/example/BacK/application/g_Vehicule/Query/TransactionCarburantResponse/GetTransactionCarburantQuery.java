package com.example.BacK.application.g_Vehicule.Query.TransactionCarburantResponse;

import com.example.BacK.domain.g_Vehicule.enumEntity.FournisseurCarburant;
import com.example.BacK.domain.g_Vehicule.enumEntity.StatutCarte;
import com.example.BacK.domain.g_Vehicule.TransactionCarburant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GetTransactionCarburantQuery {
    private String id;
    private String numero;
    private String vehiculeId;
    private String vehiculeSerie;
    private String vehiculeMarque;
    private Double plafondMensuel;
    private Double consommationMensuelle;
    private StatutCarte statut;
    private FournisseurCarburant fournisseur;
    private List<TransactionCarburant> transactions;
    private double consommation;
    private String carteTelepeageId;
    private Double montantTelepeage;
    private Double kilometrage;
    private Double ancienkilometrage;


}
