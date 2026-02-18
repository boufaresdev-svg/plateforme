package com.example.BacK.application.models.g_Client;

import com.example.BacK.domain.g_Client.Client;
import com.example.BacK.domain.g_Client.enumEntity.StatutFacture;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FactureClientDTO {

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
    private ProjetClientDTO projet;
}