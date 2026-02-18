package com.example.BacK.application.models.g_rh;

import com.example.BacK.domain.g_RH.Prime;
import com.example.BacK.domain.g_RH.Retenue;
import com.example.BacK.domain.g_RH.enumEntity.StatutRegle;
import com.example.BacK.domain.g_RH.enumEntity.TypeRegle;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RegleDTO {

    private String id;
    private String libelle;
    private String description;
    private int minAnciennete;
    private int maxAnciennete;
    private Double montantFixe;
    private Double pourcentageSalaire;
    private Integer nombreJours;
    private Integer nombrePoints;
    private TypeRegle typeRegle;
    private StatutRegle statut;
    private LocalDate dateCreation;
    private LocalDate dateModification;
    private String conditions;
    private boolean automatique;
    private PrimeDTO prime;
    private RetenueDTO retenue;
}
