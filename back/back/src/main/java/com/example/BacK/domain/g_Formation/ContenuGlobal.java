package com.example.BacK.domain.g_Formation;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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
@ToString(exclude = {"formation", "objectifsSpecifiques"})
public class ContenuGlobal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idContenuGlobal;
    private String titre;
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "formation_id")
    private Formation formation;

    @OneToMany(mappedBy = "contenuGlobal", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ObjectifSpecifique> objectifsSpecifiques = new ArrayList<>();

}
