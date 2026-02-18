package com.example.BacK.domain.g_Stock;

import com.example.BacK.domain.Auditable;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "MARQUE", uniqueConstraints = @UniqueConstraint(columnNames = "nom"))
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Marque extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36)
    private String id;

    @Column(name = "nom", unique = true, nullable = false, length = 100)
    private String nom;

    @Column(name = "code_marque", length = 50)
    private String codeMarque;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "pays", length = 100)
    private String pays;

    @Column(name = "site_web", length = 255)
    private String siteWeb;

    @Column(name = "url_logo", length = 500)
    private String urlLogo;

    @Column(name = "nom_contact", length = 100)
    private String nomContact;

    @Column(name = "email", length = 150)
    private String email;

    @Column(name = "telephone", length = 20)
    private String telephone;

    @Column(name = "poste", length = 50)
    private String poste;

    @Column(name = "est_actif", nullable = false)
    private Boolean estActif = true;
}
