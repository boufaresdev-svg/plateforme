package com.example.BacK.domain.g_Formation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"jourFormation", "levels", "contenuJourAssignments"})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ContenuDetaille {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idContenuDetaille;

    private String titre; // Content title/name

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "contenu_cles_items", joinColumns = @JoinColumn(name = "contenu_detaille_id"))
    @Column(name = "item", columnDefinition = "TEXT")
    private List<String> contenusCles = new ArrayList<>();

    @Column(columnDefinition = "TEXT")
    private String methodesPedagogiques;

    private Double dureeTheorique;
    private Double dureePratique;
    
    @Column(columnDefinition = "TEXT")
    private String tags;

    /**
     * Multi-level content support (1-3 levels: Débutant, Intermédiaire, Avancé).
     * Each level contains its own content and multiple file attachments.
     * Using Set instead of List to avoid MultipleBagFetchException.
     */
    @OneToMany(mappedBy = "contenuDetaille", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference("contenuDetaille-levels")
    private Set<ContentLevel> levels = new HashSet<>();

    // Optional: Can be assigned later via drag-and-drop
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jour_formation_id", nullable = true)
    @JsonIgnore // not needed in search responses; prevents lazy serialization issues
    private JourFormation jourFormation;

    // One-to-many back reference to ContenuJourNiveauAssignment (replaces old direct ManyToMany)
    @OneToMany(mappedBy = "contenuDetaille", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore // avoid deep recursion when serializing search results
    private List<ContenuJourNiveauAssignment> contenuJourAssignments = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContenuDetaille that = (ContenuDetaille) o;
        return idContenuDetaille != null && Objects.equals(idContenuDetaille, that.idContenuDetaille);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
