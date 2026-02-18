package com.example.BacK.application.models.g_vehicule;

import com.example.BacK.domain.g_Vehicule.enumEntity.FournisseurCarburant;
import com.example.BacK.domain.g_Vehicule.enumEntity.StatutCarte;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CarteGazoilDTO {

    private String id;
    private String numero;
    private String vehiculeId;
    private String vehiculeSerie;
    private String vehiculeMarque;
    private LocalDate dateEmission;
    private LocalDate dateExpiration;
    private Double plafondMensuel;
    private Double consommationMensuelle;
    private StatutCarte statut;
    private FournisseurCarburant fournisseur;
    private List<TransactionCarburantDTO> transactions;

}
