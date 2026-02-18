package com.example.BacK.domain.g_Projet;

import com.example.BacK.domain.Auditable;
import com.example.BacK.domain.g_RH.Employee;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Charge  extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    private String id;
    private String nom;
    private String description;
    private double montant;
    private String categorie ;
    private String sousCategorie;

    @ManyToOne
    @JoinColumn(name = "tache_id")
    private Tache tache;

    @ManyToOne
    @JoinColumn(name = "employe_id")
    private Employee employe;
}
