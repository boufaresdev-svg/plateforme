package com.example.BacK.domain.g_Formation;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a level of content (1-3) for a contenu detaille
 * Levels: 1=Débutant, 2=Intermédiaire, 3=Avancé
 * Each level contains rich text content and can have multiple file attachments
 */
@Entity
@Table(name = "content_level")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"contenuDetaille"})
public class ContentLevel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "level_number")
    private Integer levelNumber; // 1=Débutant, 2=Intermédiaire, 3=Avancé

    @Column(name = "theorie_content", columnDefinition = "NVARCHAR(MAX)")
    private String theorieContent; // Rich text content for this level
    
    @Column(name = "pratique_content", columnDefinition = "NVARCHAR(MAX)")
    private String pratiqueContent; // Practical content for this level

    @Column(name = "duree_theorique")
    private Double dureeTheorique; // Duration in hours

    @Column(name = "duree_pratique")
    private Double dureePratique; // Duration in hours

    // Formation levels this content is applicable to (Débutant, Intermédiaire, Avancé)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "content_level_formation_levels", joinColumns = @JoinColumn(name = "content_level_id"))
    @Column(name = "formation_level")
    private List<String> formationLevels = new ArrayList<>();

    // Multiple file attachments per level
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "content_level_files", joinColumns = @JoinColumn(name = "content_level_id"))
    private List<LevelFile> files = new ArrayList<>();

    // Back reference to ContenuDetaille
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contenu_detaille_id")
    @JsonBackReference("contenuDetaille-levels")
    private ContenuDetaille contenuDetaille;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContentLevel that = (ContentLevel) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
