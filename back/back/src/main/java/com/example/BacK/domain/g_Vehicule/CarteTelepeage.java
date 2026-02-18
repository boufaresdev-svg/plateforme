package com.example.BacK.domain.g_Vehicule;

import com.example.BacK.domain.g_Vehicule.enumEntity.FournisseurTelepeage;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CarteTelepeage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    private String id;

    private String numero;
    private LocalDate dateEmission;
    private Double solde;
    private Double consomation ;

    @Enumerated(EnumType.STRING)
    private FournisseurTelepeage fournisseur;


}