package com.example.BacK.application.models.g_Client;

import com.example.BacK.domain.g_Client.Client;
import com.example.BacK.domain.g_Client.enumEntity.MethodePaiement;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaiementClientDTO {

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
    private FactureClientDTO facture;
}
