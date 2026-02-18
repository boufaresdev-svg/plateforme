package com.example.BacK.application.g_Formation.Query.formation.statistics;

import com.example.BacK.domain.g_Formation.PlanFormation;
import com.example.BacK.domain.g_Formation.Formation;
import com.example.BacK.domain.g_Formation.enumEntity.StatutFormation;
import com.example.BacK.domain.g_Formation.enumEntity.TypeFormation;
import com.example.BacK.infrastructure.repository.g_Formation.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FormationStatisticsService {
    
    private final FormationRepository formationRepository;
    private final PlanFormationRepository planFormationRepository;
    private final CertificatRepository certificatRepository;
    private final ContenuDetailleRepository contenuDetailleRepository;
    private final FormateurRepository formateurRepository;
    private final DomaineRepository domaineRepository;
    private final CategorieRepository categorieRepository;
    private final SousCategorieRepository sousCategorieRepository;
    
    public FormationStatisticsResponse getStatistics() {
        List<Formation> allFormations = formationRepository.findAll();
        List<PlanFormation> allPlans = planFormationRepository.findAll();
        
        // Count formations by status
        long enCours = allPlans.stream()
                .filter(p -> p.getStatusFormation() == StatutFormation.EN_COURS)
                .count();
        long planifiees = allPlans.stream()
                .filter(p -> p.getStatusFormation() == StatutFormation.PLANIFIEE)
                .count();
        long terminees = allPlans.stream()
                .filter(p -> p.getStatusFormation() == StatutFormation.TERMINEE)
                .count();
        long annulees = allPlans.stream()
                .filter(p -> p.getStatusFormation() == StatutFormation.ANNULEE)
                .count();
        long reportees = allPlans.stream()
                .filter(p -> p.getStatusFormation() == StatutFormation.REPORTEE)
                .count();
        
        // Count active plans
        long activePlans = allPlans.stream()
                .filter(p -> p.getStatusFormation() == StatutFormation.EN_COURS 
                         || p.getStatusFormation() == StatutFormation.PLANIFIEE)
                .count();
        
        // Formation types distribution
        Map<String, Long> formationsByType = new HashMap<>();
        for (Formation f : allFormations) {
            String type = f.getTypeFormation() != null ? f.getTypeFormation().name() : "UNKNOWN";
            formationsByType.merge(type, 1L, Long::sum);
        }
        
        // Level distribution
        Map<String, Long> formationsByLevel = new HashMap<>();
        for (Formation f : allFormations) {
            String level = f.getNiveau() != null ? f.getNiveau().name() : "UNKNOWN";
            formationsByLevel.merge(level, 1L, Long::sum);
        }
        
        // Financial stats
        double totalRevenue = allFormations.stream()
                .filter(f -> f.getPrix() != null)
                .mapToDouble(Formation::getPrix)
                .sum();
        
        double avgPrice = allFormations.stream()
                .filter(f -> f.getPrix() != null && f.getPrix() > 0)
                .mapToDouble(Formation::getPrix)
                .average()
                .orElse(0.0);
        
        // Count certifications
        long totalCertifications = certificatRepository.count();
        
        // Count participants (apprenants) from plans
        long totalParticipants = allPlans.stream()
                .filter(p -> p.getApprenants() != null)
                .mapToLong(p -> p.getApprenants().size())
                .sum();
        
        // Active participants (in EN_COURS formations)
        long participantsActifs = allPlans.stream()
                .filter(p -> p.getStatusFormation() == StatutFormation.EN_COURS && p.getApprenants() != null)
                .mapToLong(p -> p.getApprenants().size())
                .sum();
        
        return FormationStatisticsResponse.builder()
                .totalFormations(allFormations.size())
                .formationsEnCours(enCours)
                .formationsPlanifiees(planifiees)
                .formationsTerminees(terminees)
                .formationsAnnulees(annulees)
                .formationsReportees(reportees)
                .totalParticipants(totalParticipants)
                .participantsActifs(participantsActifs)
                .certificationsDelivrees(totalCertifications)
                .totalContenus(contenuDetailleRepository.count())
                .totalFormateurs(formateurRepository.count())
                .formationsByType(formationsByType)
                .formationsByLevel(formationsByLevel)
                .formationsThisMonth(0L) // No creation date field on Formation
                .certificationsThisMonth(0L) // Certificat doesn't have date field
                .totalRevenue(totalRevenue)
                .averageFormationPrice(avgPrice)
                .totalDomaines(domaineRepository.count())
                .totalCategories(categorieRepository.count())
                .totalSousCategories(sousCategorieRepository.count())
                .totalPlans((long) allPlans.size())
                .activePlans(activePlans)
                .build();
    }
}
