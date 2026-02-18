package com.example.BacK.domain.g_RH;

import com.example.BacK.domain.Auditable;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Prime extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    private String id;

    @Column(nullable = false)
    private String libelle;

    @Column(nullable = false)
    private double montant;

    @Column(nullable = false)
    private int nombrePoints;

    @Column(length = 500)
    private String description;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;


    @OneToMany(mappedBy = "prime")
    private List<Regle> regles ;
}
