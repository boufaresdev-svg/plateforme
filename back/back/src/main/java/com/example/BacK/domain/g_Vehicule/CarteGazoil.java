package com.example.BacK.domain.g_Vehicule;

import com.example.BacK.domain.Auditable;
import com.example.BacK.domain.g_Vehicule.enumEntity.FournisseurCarburant;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CarteGazoil extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    private String id;

    private String numero;
    private LocalDate dateEmission;
    private Double solde;
    private Double consomation ;

    @Enumerated(EnumType.STRING)
    private FournisseurCarburant fournisseur;

    @OneToMany(mappedBy = "carte", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransactionCarburant> transactions;
}