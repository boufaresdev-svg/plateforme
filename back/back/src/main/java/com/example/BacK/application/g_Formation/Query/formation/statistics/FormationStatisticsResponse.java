package com.example.BacK.application.g_Formation.Query.formation.statistics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FormationStatisticsResponse {
    // Main counts
    private long totalFormations;
    private long formationsEnCours;
    private long formationsPlanifiees;
    private long formationsTerminees;
    private long formationsAnnulees;
    private long formationsReportees;
    
    // Participants
    private long totalParticipants;
    private long participantsActifs;
    
    // Certifications
    private long certificationsDelivrees;
    
    // Content
    private long totalContenus;
    private long totalFormateurs;
    
    // Formation types distribution
    private Map<String, Long> formationsByType;
    
    // Level distribution
    private Map<String, Long> formationsByLevel;
    
    // Trends (this month)
    private long formationsThisMonth;
    private long certificationsThisMonth;
    
    // Financial
    private Double totalRevenue;
    private Double averageFormationPrice;
    
    // Domains
    private long totalDomaines;
    private long totalCategories;
    private long totalSousCategories;
    
    // Plans
    private long totalPlans;
    private long activePlans;
}
