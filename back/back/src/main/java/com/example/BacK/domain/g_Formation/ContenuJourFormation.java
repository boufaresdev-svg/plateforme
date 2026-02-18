package com.example.BacK.domain.g_Formation;

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
@ToString(exclude = {"objectifSpecifique", "evaluations", "planFormation", "contenuAssignments"})
public class ContenuJourFormation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idContenuJour;

    private String contenu;
    private String moyenPedagogique;
    private String supportPedagogique;
    private int nbHeuresTheoriques;
    private int nbHeuresPratiques;
    
    @Column(name = "ordre")
    private Integer ordre = 0;
    
    @Column(name = "numero_jour")
    private Integer numeroJour = 1;
    
    @Column(name = "staff")
    private String staff;
    
    @Column(name = "niveau")
    private String niveau;
    
    @Column(columnDefinition = "TEXT")
    private String tags;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_formation_id")
    private PlanFormation planFormation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "objectif_specifique_id")
    private ObjectifSpecifique objectifSpecifique;

    @OneToMany(mappedBy = "contenuJourFormation", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Evaluation> evaluations = new ArrayList<>();

    // Many-to-many relationship with ContenuDetaille via junction entity with level tracking
    // Using Set instead of List to avoid ConcurrentModificationException with nested eager fetches
    @OneToMany(mappedBy = "contenuJour", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<ContenuJourNiveauAssignment> contenuAssignments = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContenuJourFormation that = (ContenuJourFormation) o;
        return idContenuJour != null && Objects.equals(idContenuJour, that.idContenuJour);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    // ==================== Backward Compatibility Methods ====================
    // These methods provide backward compatibility with code that uses the old Direct relationship
    
    /**
     * Get ContenuDetaille list from assignments (backward compatible)
     */
    @Transient
    public List<ContenuDetaille> getContenusDetailles() {
        if (contenuAssignments == null || contenuAssignments.isEmpty()) {
            return new ArrayList<>();
        }
        return contenuAssignments.stream()
                .map(ContenuJourNiveauAssignment::getContenuDetaille)
                .toList();
    }

    /**
     * Set ContenuDetaille list by creating assignments (backward compatible)
     */
    @Transient
    public void setContenusDetailles(List<ContenuDetaille> contenus) {
        if (contenuAssignments == null) {
            contenuAssignments = new HashSet<>();
        }
        contenuAssignments.clear();
        
        if (contenus != null && !contenus.isEmpty()) {
            for (ContenuDetaille contenu : contenus) {
                ContenuJourNiveauAssignment assignment = new ContenuJourNiveauAssignment();
                assignment.setContenuJour(this);
                assignment.setContenuDetaille(contenu);
                assignment.setNiveau(1); // Default to Débutant
                assignment.setNiveauLabel("DEBUTANT");
                contenuAssignments.add(assignment);
            }
        }
    }
}
