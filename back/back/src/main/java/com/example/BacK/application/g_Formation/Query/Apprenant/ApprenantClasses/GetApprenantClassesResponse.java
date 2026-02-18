package com.example.BacK.application.g_Formation.Query.Apprenant.ApprenantClasses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetApprenantClassesResponse {
    private Long apprenantId;
    private int totalClasses;
    private List<ClasseInfo> classes;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ClasseInfo {
        private Long id;
        private String nom;
        private String code;
        private String description;
        private Integer capaciteMax;
        private Boolean isActive;
        private LocalDate dateDebutAcces;
        private LocalDate dateFinAcces;
        private int nombreInscrits;
        private FormationInfo formation;
        private PlanFormationInfo planFormation;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FormationInfo {
        private Long id;
        private String nom;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PlanFormationInfo {
        private Long id;
        private String nom;
    }
}
