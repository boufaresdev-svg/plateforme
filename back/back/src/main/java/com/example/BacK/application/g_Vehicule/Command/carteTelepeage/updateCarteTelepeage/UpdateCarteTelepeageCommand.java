package com.example.BacK.application.g_Vehicule.Command.carteTelepeage.updateCarteTelepeage;

import com.example.BacK.domain.g_Vehicule.enumEntity.FournisseurTelepeage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCarteTelepeageCommand {
    private String id;
    private String numero;
    private LocalDate dateEmission;
    private Double solde;
    private Double consomation ;
    private FournisseurTelepeage fournisseur;
}
