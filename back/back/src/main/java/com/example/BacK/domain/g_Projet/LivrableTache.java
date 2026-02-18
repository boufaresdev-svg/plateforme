package com.example.BacK.domain.g_Projet;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class LivrableTache {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    @EqualsAndHashCode.Include
    private String id;
    private String nom;
    private String description;
    @ManyToOne
    @JoinColumn(name = "tache_id")
    private Tache tache;
}
