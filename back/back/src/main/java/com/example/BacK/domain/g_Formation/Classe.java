package com.example.BacK.domain.g_Formation;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe entity represents a group/class of apprenants.
 * A Classe can be associated with:
 * - An entire Formation (course) - for general classes
 * - A specific PlanFormation - for session-specific classes
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"formation", "planFormation", "apprenants"})
public class Classe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(length = 1000)
    private String description;

    @Column(unique = true)
    private String code;

    private Integer capaciteMax;

    @Column(nullable = false)
    private Boolean isActive = true;

    // Access date range - when the class is accessible
    @Column(name = "date_debut_acces")
    private LocalDate dateDebutAcces;

    @Column(name = "date_fin_acces")
    private LocalDate dateFinAcces;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // A class can be linked to an entire Formation (course)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "formation_id")
    private Formation formation;

    // Or to a specific PlanFormation (session)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_formation_id")
    private PlanFormation planFormation;

    // Apprenants in this class
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "classe_apprenant",
        joinColumns = @JoinColumn(name = "classe_id"),
        inverseJoinColumns = @JoinColumn(name = "apprenant_id")
    )
    private List<Apprenant> apprenants = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (isActive == null) isActive = true;
        if (code == null || code.isEmpty()) {
            code = generateCode();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    private String generateCode() {
        String prefix = "CLS";
        String timestamp = String.valueOf(System.currentTimeMillis()).substring(6);
        return prefix + timestamp;
    }

    // Helper method to get the effective formation
    public Formation getEffectiveFormation() {
        if (formation != null) {
            return formation;
        }
        if (planFormation != null && planFormation.getFormation() != null) {
            return planFormation.getFormation();
        }
        return null;
    }

    // Helper method to check if class is full
    public boolean isFull() {
        if (capaciteMax == null || capaciteMax <= 0) {
            return false;
        }
        return apprenants.size() >= capaciteMax;
    }

    // Helper to get current enrollment count
    public int getEnrollmentCount() {
        return apprenants != null ? apprenants.size() : 0;
    }

    // Helper to check if class is currently accessible based on date range
    public boolean isAccessible() {
        LocalDate today = LocalDate.now();
        if (dateDebutAcces != null && today.isBefore(dateDebutAcces)) {
            return false;
        }
        if (dateFinAcces != null && today.isAfter(dateFinAcces)) {
            return false;
        }
        return isActive;
    }
}
