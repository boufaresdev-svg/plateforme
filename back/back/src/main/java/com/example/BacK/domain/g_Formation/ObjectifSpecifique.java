package com.example.BacK.domain.g_Formation;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
@ToString(exclude = {"contenuGlobal", "contenusJourFormation", "objectifGlobal"})
public class ObjectifSpecifique {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idObjectifSpec;

    private String titre;
    private String description;
    
    @Column(columnDefinition = "TEXT")
    private String tags;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "objectif_global_id")
    @JsonBackReference
    private ObjectifGlobal objectifGlobal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contenu_global_id")
    @JsonBackReference
    private ContenuGlobal contenuGlobal;

    @OneToMany(mappedBy = "objectifSpecifique", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ContenuJourFormation> contenusJourFormation = new ArrayList<>();

}
