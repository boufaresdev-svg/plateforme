package com.example.BacK.domain.g_Client;

import com.example.BacK.domain.g_Client.enumEntity.StatutFacture;
import com.example.BacK.domain.g_Projet.Projet;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class FactureClient {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    @EqualsAndHashCode.Include
    private String id;

    private String numero;
    private double montant;

    private LocalDate dateEmission;
    private LocalDate dateEcheance;

    @Enumerated(EnumType.STRING)
    private StatutFacture statut;

    private String description;

    // Relation vers Client
    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    // Relation vers ProjetClient (optionnelle)
    @ManyToOne
    @JoinColumn(name = "projet_id")
    private Projet projet;
}