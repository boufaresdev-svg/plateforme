package com.example.BacK.domain.g_Formation;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;

/**
 * Junction entity to track the assignment of ContenuDetaille to ContenuJourFormation
 * with the specific level (Débutant, Intermédiaire, Avancé) it's assigned to
 */
@Entity
@Table(name = "contenu_jour_niveau_assignment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"contenuJour", "contenuDetaille"})
public class ContenuJourNiveauAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contenu_jour_id", nullable = false)
    private ContenuJourFormation contenuJour;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contenu_detaille_id", nullable = false)
    private ContenuDetaille contenuDetaille;

    /**
     * Level: 1 = Débutant, 2 = Intermédiaire, 3 = Avancé
     */
    @Column(nullable = false)
    private Integer niveau;

    /**
     * Optional: Level name for easier reference (DEBUTANT, INTERMEDIAIRE, AVANCE)
     */
    @Column(length = 50)
    private String niveauLabel;

    /**
     * Timestamp of assignment
     */
    @Column(name = "assigned_at", nullable = false, updatable = false)
    private java.time.LocalDateTime assignedAt = java.time.LocalDateTime.now();

    /**
     * Constructor with main fields
     */
    public ContenuJourNiveauAssignment(ContenuJourFormation contenuJour, ContenuDetaille contenuDetaille, Integer niveau, String niveauLabel) {
        this.contenuJour = contenuJour;
        this.contenuDetaille = contenuDetaille;
        this.niveau = niveau;
        this.niveauLabel = niveauLabel;
        this.assignedAt = java.time.LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContenuJourNiveauAssignment that = (ContenuJourNiveauAssignment) o;
        // Use id if available, otherwise use object identity
        if (id != null && that.id != null) {
            return Objects.equals(id, that.id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        // Use a constant for new entities to allow adding to Set before persisting
        return getClass().hashCode();
    }
}
