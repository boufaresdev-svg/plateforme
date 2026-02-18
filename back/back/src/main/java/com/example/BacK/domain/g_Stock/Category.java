package com.example.BacK.domain.g_Stock;

import com.example.BacK.domain.Auditable;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "CATEGORIE_ARTICLE", uniqueConstraints = @UniqueConstraint(columnNames = "nom"))
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Category extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    private String id;

    @Column(name = "nom", unique = true, nullable = false)
    private String nom;

    @Column(name = "description")
    private String description;

    @Column(name = "est_actif", nullable = false)
    private Boolean estActif = true;
}