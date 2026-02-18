package com.example.BacK.domain.g_Client;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ContactClient {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    @EqualsAndHashCode.Include
    private String id;

    private String nom;
    private String prenom;
    private String poste;
    private String telephone;
    private String email;
    private boolean principal;

    @ManyToOne
    @JoinColumn(name = "client_id") // clé étrangère dans la table contact_client
    private Client client;
}