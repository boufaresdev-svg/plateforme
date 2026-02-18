package com.example.BacK.application.models.g_Client;

import com.example.BacK.domain.g_Client.Client;
import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContactClientDTO {

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
    @JoinColumn(name = "client_id")
    private Client client;
}