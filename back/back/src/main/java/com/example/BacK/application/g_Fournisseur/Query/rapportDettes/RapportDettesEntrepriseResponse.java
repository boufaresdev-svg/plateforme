package com.example.BacK.application.g_Fournisseur.Query.rapportDettes;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RapportDettesEntrepriseResponse {
    
    // Informations générales du rapport
    private InfosGeneralesRapport infosGenerales;
    
    // Synthèse financière globale
    private SyntheseFinanciere syntheseFinanciere;
    
    // Analyse par fournisseur
    private List<AnalyseFournisseur> analyseParFournisseur;
    
    // Analyse par statut de paiement
    private AnalyseStatut analyseStatut;
    
    // Analyse des retards
    private AnalyseRetards analyseRetards;
    
    // Analyse temporelle
    private AnalyseTemporelle analyseTemporelle;
    
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class InfosGeneralesRapport {
        private LocalDate dateGeneration;
        private LocalDate periodeDebut;
        private LocalDate periodeFin;
        private Integer nombreTotalDettes;
        private Integer nombreFournisseursActifs;
    }
    
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SyntheseFinanciere {
        private Double montantTotalDettes;
        private Double montantTotalPaye;
        private Double montantTotalImpaye;
        private Double montantEnRetard;
        private Double tauxPaiement; // Pourcentage
        private Double montantMoyenParDette;
        private Double montantMoyenParFournisseur;
    }
    
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AnalyseFournisseur {
        private String fournisseurId;
        private String nomFournisseur;
        private String infoContact;
        private Integer nombreDettes;
        private Double montantTotal;
        private Double montantPaye;
        private Double montantImpaye;
        private Integer nombreDettesEnRetard;
        private Double montantEnRetard;
        private Double tauxPaiement;
        private String evaluation; // "BON", "MOYEN", "CRITIQUE"
        private Integer ancienneteMoyenneJours;
    }
    
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AnalyseStatut {
        private Integer nombreDettesPayees;
        private Integer nombreDettesImpayes;
        private Double montantDettesPayees;
        private Double montantDettesImpayes;
        private Double pourcentageDettesPayees;
        private Double pourcentageDettesImpayes;
    }
    
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AnalyseRetards {
        private Integer nombreDettesEnRetard;
        private Double montantTotalEnRetard;
        private Double pourcentageDettesEnRetard;
        private Integer retardMoyenJours;
        private Integer retardMaximumJours;
        private List<DetteEnRetard> top5DettesEnRetard;
    }
    
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DetteEnRetard {
        private String detteId;
        private String numeroFacture;
        private String fournisseurNom;
        private Double montant;
        private LocalDate dateEcheance;
        private Integer joursRetard;
    }
    
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AnalyseTemporelle {
        private DistributionMensuelle distributionMensuelle;
        private TendancesPaiement tendancesPaiement;
    }
    
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DistributionMensuelle {
        private List<DonneesMensuelle> donneesParMois;
    }
    
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DonneesMensuelle {
        private String mois; // Format: "Janvier 2024"
        private Integer nombreDettes;
        private Double montantTotal;
        private Double montantPaye;
        private Double montantImpaye;
    }
    
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TendancesPaiement {
        private Double delaiMoyenPaiementJours;
        private Double tauxRespectEcheances;
        private String tendanceGenerale; // "EN_AMELIORATION", "STABLE", "EN_DETERIORATION"
    }
}
