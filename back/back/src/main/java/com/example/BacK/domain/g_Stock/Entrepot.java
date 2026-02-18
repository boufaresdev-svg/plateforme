package com.example.BacK.domain.g_Stock;

import com.example.BacK.domain.Auditable;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ENTREPOT", uniqueConstraints = @UniqueConstraint(columnNames = "nom"))
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Entrepot extends Auditable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    private String id;
    
    @Column(name = "nom", unique = true, nullable = false, length = 100)
    private String nom;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "adresse", nullable = false, length = 255)
    private String adresse;
    
    @Column(name = "ville", nullable = false, length = 100)
    private String ville;
    
    @Column(name = "code_postal", length = 20)
    private String codePostal;
    
    @Column(name = "telephone", length = 20)
    private String telephone;
    
    @Column(name = "email", length = 150)
    private String email;
    
    @Column(name = "responsable", length = 100)
    private String responsable;
    
    @Column(name = "superficie")
    private Double superficie;
    
    @Column(name = "capacite_maximale")
    private Double capaciteMaximale;
    
    @Column(name = "capacite_utilisee")
    private Double capaciteUtilisee = 0.0;
    
    @Column(name = "statut", length = 50)
    private String statut;
    
    @Column(name = "est_actif", nullable = false)
    private Boolean estActif = true;
}
