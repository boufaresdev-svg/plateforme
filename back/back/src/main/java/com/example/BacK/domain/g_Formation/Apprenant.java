package com.example.BacK.domain.g_Formation;

import com.example.BacK.domain.g_Formation.enumEntity.StatusInscription;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"planFormation", "examens", "certificats", "evaluations"})
public class Apprenant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true)
    private String matricule;
    
    private String nom;
    private String prenom;
    private String adresse;
    private String telephone;
    
    @Column(unique = true)
    private String email;
    
    private String prerequis;
    
    // Authentication fields
    @Column(name = "password")
    private String password;
    
    // Status fields - nullable to allow adding to existing table
    @Column(name = "is_blocked")
    private Boolean isBlocked;
    
    @Column(name = "is_staff")
    private Boolean isStaff;
    
    @Column(name = "is_active")
    private Boolean isActive;
    
    @Column(name = "last_login")
    private LocalDateTime lastLogin;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    private StatusInscription statusInscription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_formation_id")
    private PlanFormation planFormation;

    @OneToMany(mappedBy = "apprenant", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Examen> examens = new ArrayList<>();

    @OneToMany(mappedBy = "apprenant", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Certificat> certificats = new ArrayList<>();

    @OneToMany(mappedBy = "apprenant", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Evaluation> evaluations = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (isBlocked == null) isBlocked = false;
        if (isStaff == null) isStaff = false;
        if (isActive == null) isActive = true;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
