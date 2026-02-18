package com.example.BacK.domain.g_Formation;

import com.example.BacK.domain.g_Formation.enumEntity.NiveauFormation;
import com.example.BacK.domain.g_Formation.enumEntity.NiveauFormationConverter;
import com.example.BacK.domain.g_Formation.enumEntity.TypeFormation;
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
@ToString(exclude = {"planFormations", "formateurs"})
public class Formation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idFormation;
    private String theme;
    
    @Column(columnDefinition = "TEXT")
    private String descriptionTheme;
    
    @Column(columnDefinition = "TEXT")
    private String objectifSpecifiqueGlobal;
    
    @Column(columnDefinition = "TEXT")
    private String tags;
    
    private Integer nombreHeures;
    private Double prix;
    private Integer nombreMax;
    private String populationCible;
    
    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private TypeFormation typeFormation;

    @Convert(converter = NiveauFormationConverter.class)
    @Column(nullable = true)
    private NiveauFormation niveau;
    
    @Column(length = 20)
    private String statut; // Brouillon or Publié

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "formation_objectif_global",
        joinColumns = @JoinColumn(name = "formation_id"),
        inverseJoinColumns = @JoinColumn(name = "objectif_global_id")
    )
    private List<ObjectifGlobal> objectifsGlobaux = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "formation_objectif_specifique",
        joinColumns = @JoinColumn(name = "formation_id"),
        inverseJoinColumns = @JoinColumn(name = "objectif_specifique_id")
    )
    private List<ObjectifSpecifique> objectifsSpecifiques = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "domaine_id")
    private Domaine domaine;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "type_id")
    private Type type;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "categorie_id")
    private Categorie categorie;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sous_categorie_id")
    private SousCategorie sousCategorie;

    @OneToMany(mappedBy = "formation", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PlanFormation> planFormations = new ArrayList<>();

    @OneToMany(mappedBy = "formation", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ProgrammeDetaile> programmesDetailes = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(joinColumns = @JoinColumn(name = "formation_id"), inverseJoinColumns = @JoinColumn(name = "formateur_id"))

    private List<Formateur> formateurs = new ArrayList<>();


}
