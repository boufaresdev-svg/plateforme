package com.example.BacK.domain.g_Formation;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"formationsResponsables", "formationsAnimees"})
public class Formateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idFormateur;

    private String nom;
    private String prenom;
    private String specialite;
    private String contact;
    private String experience;
    private String documentUrl;

    @ManyToMany(mappedBy = "formateurs", fetch = FetchType.LAZY)
    private List<Formation> formationsAnimees = new ArrayList<>();

    @OneToMany(mappedBy = "formateur", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PlanFormation> formationsResponsables = new ArrayList<>();
}
