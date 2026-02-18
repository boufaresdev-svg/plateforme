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
@ToString(exclude = {"formation", "joursFormation"})
public class ProgrammeDetaile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProgramme;

    private String titre; // Objectif Spécifique title

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "formation_id")
    private Formation formation;

    @OneToMany(mappedBy = "programmeDetaile", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<JourFormation> joursFormation = new ArrayList<>();
}
