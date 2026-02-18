package com.example.BacK.application.g_Vehicule.Query.CarteTelepeage;

import com.example.BacK.application.models.g_vehicule.TransactionCarburantDTO;
import com.example.BacK.domain.g_Vehicule.enumEntity.FournisseurCarburant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class GetCarteTelepeageQuery {

    private String id;
    private String numero;
    private LocalDate dateEmission;
    private Double solde;
    private Double consomation ;
    private FournisseurCarburant fournisseur;
    private List<TransactionCarburantDTO> transactions;
}
