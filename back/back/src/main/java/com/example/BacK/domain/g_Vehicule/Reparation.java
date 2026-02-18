package com.example.BacK.domain.g_Vehicule;

import com.example.BacK.domain.Auditable;
import com.example.BacK.domain.g_Vehicule.enumEntity.TypeReparation;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Reparation extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    private String id;
    private String url;

    @Enumerated(EnumType.STRING)
    private TypeReparation type;

    private Double prix;
    private LocalDate date;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne
    @JoinColumn(name = "vehicule_id")
    private Vehicule vehicule;
}