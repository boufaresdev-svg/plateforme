package com.example.BacK.application.g_Vehicule.Command.carteGazole.addGazoil;

import com.example.BacK.application.models.g_vehicule.TransactionCarburantDTO;
import com.example.BacK.domain.g_Vehicule.enumEntity.FournisseurCarburant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddCarteGazoilCommand {

    private String numero;
    private LocalDate dateEmission;
    private Double solde;
    private Double consomation ;
    private FournisseurCarburant fournisseur;
    private List<TransactionCarburantDTO> transactions;

}