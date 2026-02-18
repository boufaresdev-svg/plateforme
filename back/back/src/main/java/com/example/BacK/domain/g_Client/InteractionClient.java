package com.example.BacK.domain.g_Client;

import com.example.BacK.domain.g_Client.enumEntity.TypeInteraction;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class InteractionClient {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    @EqualsAndHashCode.Include
    private String id;

    @Enumerated(EnumType.STRING)
    private TypeInteraction type;

    private String sujet;
    private String description;
    private LocalDate date;
    private String responsable;
    private boolean suiviRequis;
    private LocalDate dateSuivi;

    // Relation vers Client
    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;
}
