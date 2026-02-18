package com.example.BacK.domain.g_Fournisseur;

import com.example.BacK.domain.Auditable;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "nom"))
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Fournisseur extends Auditable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    private String id;
    
    @Column(unique = true, nullable = false)
    private String nom;
    private String infoContact;
    private String adresse;
    private String telephone;
    private String matriculeFiscale;
    
    @OneToMany(mappedBy = "fournisseur", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DettesFournisseur> dettes;
}
