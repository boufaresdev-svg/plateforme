package com.example.BacK.domain.g_Client;

import com.example.BacK.domain.g_Client.enumEntity.MethodePaiement;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PaiementClient {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    @EqualsAndHashCode.Include
    private String id;

    private double montant;
    private LocalDate datePaiement;

    @Enumerated(EnumType.STRING)
    private MethodePaiement methode;

    // Relation vers Client
    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    // Relation vers FactureClient
    @ManyToOne
    @JoinColumn(name = "facture_id")
    private FactureClient facture;
}
