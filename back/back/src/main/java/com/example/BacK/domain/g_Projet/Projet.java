package com.example.BacK.domain.g_Projet;

import com.example.BacK.domain.Auditable;
import com.example.BacK.domain.g_Client.Client;
import com.example.BacK.domain.g_Client.FactureClient;
import com.example.BacK.domain.g_Projet.enumEntity.PrioriteProjet;
import com.example.BacK.domain.g_Projet.enumEntity.StatutProjet;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Projet extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    @EqualsAndHashCode.Include
    private String id;

    private String nom;
    private String description;

    @Column(length = 100)
    private String type;

    @Enumerated(EnumType.STRING)
    private StatutProjet statut;

    @Enumerated(EnumType.STRING)
    private PrioriteProjet priorite;

    @Column(length = 100)
    private String chefProjet;

    private LocalDate dateDebut;
    private LocalDate dateFin;
    private LocalDate dateFinPrevue;
    private Double budget;
    private Double coutReel;
    private Double progression;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @OneToMany(mappedBy = "projet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Mission> missions = new ArrayList<>();

    @OneToMany(mappedBy = "projet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Phase> phases = new ArrayList<>();

    @OneToMany(mappedBy = "projet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FactureClient> factures = new ArrayList<>();
    @ElementCollection
    @CollectionTable(name = "projet_documents", joinColumns = @JoinColumn(name = "projet_id"))
    @Column(name = "document")
    private List<String> documents = new ArrayList<>();
}



