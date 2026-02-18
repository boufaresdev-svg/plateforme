package com.example.BacK.application.g_Fournisseur.Query.rapportDettes;

import com.example.BacK.application.mediator.RequestHandler;
import com.example.BacK.domain.g_Fournisseur.DettesFournisseur;
import com.example.BacK.domain.g_Fournisseur.Fournisseur;
import com.example.BacK.domain.g_Fournisseur.TranchePaiement;
import com.example.BacK.infrastructure.repository.g_Fournisseur.DettesFournisseurRepository;
import com.example.BacK.infrastructure.repository.g_Fournisseur.FournisseurRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Component("RapportDettesEntrepriseQueryHandler")
@AllArgsConstructor
public class RapportDettesEntrepriseQueryHandler implements RequestHandler<RapportDettesEntrepriseQuery, RapportDettesEntrepriseResponse> {

    private final DettesFournisseurRepository dettesFournisseurRepository;
    private final FournisseurRepository fournisseurRepository;

    @Override
    public RapportDettesEntrepriseResponse handle(RapportDettesEntrepriseQuery query) {
        // Récupérer toutes les dettes avec leurs fournisseurs
        List<DettesFournisseur> toutesLesDettes = dettesFournisseurRepository.findAllWithFournisseur();
        
        // Filtrer par fournisseur si spécifié
        if (query.getFournisseurId() != null && !query.getFournisseurId().isEmpty()) {
            toutesLesDettes = toutesLesDettes.stream()
                .filter(dette -> dette.getFournisseur() != null && 
                                query.getFournisseurId().equals(dette.getFournisseur().getId()))
                .collect(Collectors.toList());
        }
        
        // Filtrer par période si spécifié
        List<DettesFournisseur> dettesFiltered = filtrerParPeriode(toutesLesDettes, query);
        
        // Filtrer les dettes payées si nécessaire
        if (!Boolean.TRUE.equals(query.getInclureDettesPayees())) {
            dettesFiltered = dettesFiltered.stream()
                .filter(dette -> !Boolean.TRUE.equals(dette.getEstPaye()))
                .collect(Collectors.toList());
        }

        // Générer les différentes sections du rapport
        RapportDettesEntrepriseResponse.InfosGeneralesRapport infosGenerales = genererInfosGenerales(query, dettesFiltered);
        RapportDettesEntrepriseResponse.SyntheseFinanciere syntheseFinanciere = genererSyntheseFinanciere(dettesFiltered);
        List<RapportDettesEntrepriseResponse.AnalyseFournisseur> analyseParFournisseur = genererAnalyseParFournisseur(dettesFiltered);
        RapportDettesEntrepriseResponse.AnalyseStatut analyseStatut = genererAnalyseStatut(dettesFiltered);
        RapportDettesEntrepriseResponse.AnalyseRetards analyseRetards = genererAnalyseRetards(dettesFiltered);
        RapportDettesEntrepriseResponse.AnalyseTemporelle analyseTemporelle = genererAnalyseTemporelle(dettesFiltered);

        return RapportDettesEntrepriseResponse.builder()
            .infosGenerales(infosGenerales)
            .syntheseFinanciere(syntheseFinanciere)
            .analyseParFournisseur(analyseParFournisseur)
            .analyseStatut(analyseStatut)
            .analyseRetards(analyseRetards)
            .analyseTemporelle(analyseTemporelle)
            .build();
    }

    private List<DettesFournisseur> filtrerParPeriode(List<DettesFournisseur> dettes, RapportDettesEntrepriseQuery query) {
        return dettes.stream()
            .filter(dette -> {
                LocalDate dateCreation = dette.getCreatedAt() != null ? 
                    dette.getCreatedAt().toLocalDate() : LocalDate.now();
                
                boolean apresDebut = query.getDateDebut() == null || 
                    !dateCreation.isBefore(query.getDateDebut());
                boolean avantFin = query.getDateFin() == null || 
                    !dateCreation.isAfter(query.getDateFin());
                
                return apresDebut && avantFin;
            })
            .collect(Collectors.toList());
    }

    private RapportDettesEntrepriseResponse.InfosGeneralesRapport genererInfosGenerales(
            RapportDettesEntrepriseQuery query, List<DettesFournisseur> dettes) {
        
        Set<String> fournisseursUniques = dettes.stream()
            .map(dette -> dette.getFournisseur().getId())
            .collect(Collectors.toSet());

        return RapportDettesEntrepriseResponse.InfosGeneralesRapport.builder()
            .dateGeneration(LocalDate.now())
            .periodeDebut(query.getDateDebut())
            .periodeFin(query.getDateFin())
            .nombreTotalDettes(dettes.size())
            .nombreFournisseursActifs(fournisseursUniques.size())
            .build();
    }

    private RapportDettesEntrepriseResponse.SyntheseFinanciere genererSyntheseFinanciere(List<DettesFournisseur> dettes) {
        double montantTotal = dettes.stream()
            .filter(d -> d.getMontantTotal() != null)
            .mapToDouble(d -> d.getMontantTotal().doubleValue())
            .sum();

        double montantPaye = dettes.stream()
            .filter(d -> Boolean.TRUE.equals(d.getEstPaye()))
            .filter(d -> d.getMontantTotal() != null)
            .mapToDouble(d -> d.getMontantTotal().doubleValue())
            .sum();

        double montantImpaye = dettes.stream()
            .filter(d -> !Boolean.TRUE.equals(d.getEstPaye()))
            .filter(d -> d.getMontantTotal() != null)
            .mapToDouble(d -> d.getMontantTotal().doubleValue())
            .sum();

        double montantEnRetard = dettes.stream()
            .filter(this::estDetteEnRetard)
            .filter(d -> d.getMontantTotal() != null)
            .mapToDouble(d -> d.getMontantTotal().doubleValue())
            .sum();

        double tauxPaiement = montantTotal > 0 ? (montantPaye / montantTotal) * 100 : 0;
        double montantMoyen = dettes.isEmpty() ? 0 : montantTotal / dettes.size();

        Set<String> fournisseursUniques = dettes.stream()
            .map(dette -> dette.getFournisseur().getId())
            .collect(Collectors.toSet());
        double montantMoyenParFournisseur = fournisseursUniques.isEmpty() ? 0 : montantTotal / fournisseursUniques.size();

        return RapportDettesEntrepriseResponse.SyntheseFinanciere.builder()
            .montantTotalDettes(arrondir(montantTotal))
            .montantTotalPaye(arrondir(montantPaye))
            .montantTotalImpaye(arrondir(montantImpaye))
            .montantEnRetard(arrondir(montantEnRetard))
            .tauxPaiement(arrondir(tauxPaiement))
            .montantMoyenParDette(arrondir(montantMoyen))
            .montantMoyenParFournisseur(arrondir(montantMoyenParFournisseur))
            .build();
    }

    private List<RapportDettesEntrepriseResponse.AnalyseFournisseur> genererAnalyseParFournisseur(List<DettesFournisseur> dettes) {
        Map<String, List<DettesFournisseur>> dettesParFournisseur = dettes.stream()
            .collect(Collectors.groupingBy(d -> d.getFournisseur().getId()));

        return dettesParFournisseur.entrySet().stream()
            .map(entry -> {
                String fournisseurId = entry.getKey();
                List<DettesFournisseur> dettesFournisseur = entry.getValue();
                Fournisseur fournisseur = dettesFournisseur.get(0).getFournisseur();

                double montantTotal = dettesFournisseur.stream()
                    .filter(d -> d.getMontantTotal() != null)
                    .mapToDouble(d -> d.getMontantTotal().doubleValue())
                    .sum();

                double montantPaye = dettesFournisseur.stream()
                    .filter(d -> Boolean.TRUE.equals(d.getEstPaye()))
                    .filter(d -> d.getMontantTotal() != null)
                    .mapToDouble(d -> d.getMontantTotal().doubleValue())
                    .sum();

                double montantImpaye = montantTotal - montantPaye;

                long nombreDettesEnRetard = dettesFournisseur.stream()
                    .filter(this::estDetteEnRetard)
                    .count();

                double montantEnRetard = dettesFournisseur.stream()
                    .filter(this::estDetteEnRetard)
                    .filter(d -> d.getMontantTotal() != null)
                    .mapToDouble(d -> d.getMontantTotal().doubleValue())
                    .sum();

                double tauxPaiement = montantTotal > 0 ? (montantPaye / montantTotal) * 100 : 0;

                String evaluation = evaluerFournisseur(tauxPaiement, nombreDettesEnRetard);

                double ancienneteMoyenne = dettesFournisseur.stream()
                    .filter(d -> d.getCreatedAt() != null)
                    .mapToLong(d -> ChronoUnit.DAYS.between(d.getCreatedAt().toLocalDate(), LocalDate.now()))
                    .average()
                    .orElse(0.0);

                return RapportDettesEntrepriseResponse.AnalyseFournisseur.builder()
                    .fournisseurId(fournisseurId)
                    .nomFournisseur(fournisseur.getNom())
                    .infoContact(fournisseur.getInfoContact())
                    .nombreDettes(dettesFournisseur.size())
                    .montantTotal(arrondir(montantTotal))
                    .montantPaye(arrondir(montantPaye))
                    .montantImpaye(arrondir(montantImpaye))
                    .nombreDettesEnRetard((int) nombreDettesEnRetard)
                    .montantEnRetard(arrondir(montantEnRetard))
                    .tauxPaiement(arrondir(tauxPaiement))
                    .evaluation(evaluation)
                    .ancienneteMoyenneJours((int) ancienneteMoyenne)
                    .build();
            })
            .sorted(Comparator.comparing(RapportDettesEntrepriseResponse.AnalyseFournisseur::getMontantTotal).reversed())
            .collect(Collectors.toList());
    }

    private RapportDettesEntrepriseResponse.AnalyseStatut genererAnalyseStatut(List<DettesFournisseur> dettes) {
        long nombrePayees = dettes.stream()
            .filter(d -> Boolean.TRUE.equals(d.getEstPaye()))
            .count();

        long nombreImpayes = dettes.size() - nombrePayees;

        double montantPayees = dettes.stream()
            .filter(d -> Boolean.TRUE.equals(d.getEstPaye()))
            .filter(d -> d.getMontantTotal() != null)
            .mapToDouble(d -> d.getMontantTotal().doubleValue())
            .sum();

        double montantImpayes = dettes.stream()
            .filter(d -> !Boolean.TRUE.equals(d.getEstPaye()))
            .filter(d -> d.getMontantTotal() != null)
            .mapToDouble(d -> d.getMontantTotal().doubleValue())
            .sum();

        double pourcentagePayees = dettes.isEmpty() ? 0 : (nombrePayees * 100.0) / dettes.size();
        double pourcentageImpayes = 100 - pourcentagePayees;

        return RapportDettesEntrepriseResponse.AnalyseStatut.builder()
            .nombreDettesPayees((int) nombrePayees)
            .nombreDettesImpayes((int) nombreImpayes)
            .montantDettesPayees(arrondir(montantPayees))
            .montantDettesImpayes(arrondir(montantImpayes))
            .pourcentageDettesPayees(arrondir(pourcentagePayees))
            .pourcentageDettesImpayes(arrondir(pourcentageImpayes))
            .build();
    }

    private RapportDettesEntrepriseResponse.AnalyseRetards genererAnalyseRetards(List<DettesFournisseur> dettes) {
        List<DettesFournisseur> dettesEnRetard = dettes.stream()
            .filter(this::estDetteEnRetard)
            .collect(Collectors.toList());

        double montantEnRetard = dettesEnRetard.stream()
            .filter(d -> d.getMontantTotal() != null)
            .mapToDouble(d -> d.getMontantTotal().doubleValue())
            .sum();

        double pourcentageEnRetard = dettes.isEmpty() ? 0 : (dettesEnRetard.size() * 100.0) / dettes.size();

        // Calculer le retard moyen et maximum
        List<Long> retards = new ArrayList<>();
        for (DettesFournisseur dette : dettesEnRetard) {
            if (dette.getTranchesPaiement() != null) {
                for (TranchePaiement tranche : dette.getTranchesPaiement()) {
                    if (estTrancheEnRetard(tranche) && tranche.getDateEcheance() != null) {
                        long joursRetard = ChronoUnit.DAYS.between(tranche.getDateEcheance(), LocalDate.now());
                        retards.add(joursRetard);
                    }
                }
            }
        }

        int retardMoyen = retards.isEmpty() ? 0 : (int) retards.stream().mapToLong(Long::longValue).average().orElse(0);
        int retardMax = retards.isEmpty() ? 0 : retards.stream().mapToInt(Long::intValue).max().orElse(0);

        // Top 5 des dettes en retard
        List<RapportDettesEntrepriseResponse.DetteEnRetard> top5 = dettesEnRetard.stream()
            .map(dette -> {
                LocalDate dateEcheancePlusAncienne = null;
                int joursRetardMax = 0;

                if (dette.getTranchesPaiement() != null) {
                    for (TranchePaiement tranche : dette.getTranchesPaiement()) {
                        if (estTrancheEnRetard(tranche) && tranche.getDateEcheance() != null) {
                            long joursRetard = ChronoUnit.DAYS.between(tranche.getDateEcheance(), LocalDate.now());
                            if (joursRetard > joursRetardMax) {
                                joursRetardMax = (int) joursRetard;
                                dateEcheancePlusAncienne = tranche.getDateEcheance();
                            }
                        }
                    }
                }

                return RapportDettesEntrepriseResponse.DetteEnRetard.builder()
                    .detteId(dette.getId())
                    .numeroFacture(dette.getNumeroFacture())
                    .fournisseurNom(dette.getFournisseur().getNom())
                    .montant(dette.getMontantTotal() != null ? arrondir(dette.getMontantTotal().doubleValue()) : 0.0)
                    .dateEcheance(dateEcheancePlusAncienne)
                    .joursRetard(joursRetardMax)
                    .build();
            })
            .sorted(Comparator.comparing(RapportDettesEntrepriseResponse.DetteEnRetard::getJoursRetard).reversed())
            .limit(5)
            .collect(Collectors.toList());

        return RapportDettesEntrepriseResponse.AnalyseRetards.builder()
            .nombreDettesEnRetard(dettesEnRetard.size())
            .montantTotalEnRetard(arrondir(montantEnRetard))
            .pourcentageDettesEnRetard(arrondir(pourcentageEnRetard))
            .retardMoyenJours(retardMoyen)
            .retardMaximumJours(retardMax)
            .top5DettesEnRetard(top5)
            .build();
    }

    private RapportDettesEntrepriseResponse.AnalyseTemporelle genererAnalyseTemporelle(List<DettesFournisseur> dettes) {
        // Distribution mensuelle
        Map<YearMonth, List<DettesFournisseur>> dettesParMois = dettes.stream()
            .filter(d -> d.getCreatedAt() != null)
            .collect(Collectors.groupingBy(d -> YearMonth.from(d.getCreatedAt())));

        List<RapportDettesEntrepriseResponse.DonneesMensuelle> donneesParMois = dettesParMois.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .map(entry -> {
                YearMonth mois = entry.getKey();
                List<DettesFournisseur> dettesMois = entry.getValue();

                double montantTotal = dettesMois.stream()
                    .filter(d -> d.getMontantTotal() != null)
                    .mapToDouble(d -> d.getMontantTotal().doubleValue())
                    .sum();

                double montantPaye = dettesMois.stream()
                    .filter(d -> Boolean.TRUE.equals(d.getEstPaye()))
                    .filter(d -> d.getMontantTotal() != null)
                    .mapToDouble(d -> d.getMontantTotal().doubleValue())
                    .sum();

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.FRENCH);
                
                return RapportDettesEntrepriseResponse.DonneesMensuelle.builder()
                    .mois(mois.format(formatter))
                    .nombreDettes(dettesMois.size())
                    .montantTotal(arrondir(montantTotal))
                    .montantPaye(arrondir(montantPaye))
                    .montantImpaye(arrondir(montantTotal - montantPaye))
                    .build();
            })
            .collect(Collectors.toList());

        RapportDettesEntrepriseResponse.DistributionMensuelle distributionMensuelle = 
            RapportDettesEntrepriseResponse.DistributionMensuelle.builder()
                .donneesParMois(donneesParMois)
                .build();

        // Tendances de paiement
        double delaiMoyen = dettes.stream()
            .filter(d -> Boolean.TRUE.equals(d.getEstPaye()))
            .filter(d -> d.getCreatedAt() != null && d.getDatePaiementReelle() != null)
            .mapToLong(d -> ChronoUnit.DAYS.between(d.getCreatedAt().toLocalDate(), d.getDatePaiementReelle()))
            .average()
            .orElse(0.0);

        long dettesAvecEcheance = dettes.stream()
            .filter(d -> d.getDatePaiementPrevue() != null)
            .count();

        long dettesPayeesATemps = dettes.stream()
            .filter(d -> Boolean.TRUE.equals(d.getEstPaye()))
            .filter(d -> d.getDatePaiementPrevue() != null && d.getDatePaiementReelle() != null)
            .filter(d -> !d.getDatePaiementReelle().isAfter(d.getDatePaiementPrevue()))
            .count();

        double tauxRespect = dettesAvecEcheance > 0 ? (dettesPayeesATemps * 100.0) / dettesAvecEcheance : 0;

        String tendance = determinerTendance(tauxRespect);

        RapportDettesEntrepriseResponse.TendancesPaiement tendancesPaiement = 
            RapportDettesEntrepriseResponse.TendancesPaiement.builder()
                .delaiMoyenPaiementJours(arrondir(delaiMoyen))
                .tauxRespectEcheances(arrondir(tauxRespect))
                .tendanceGenerale(tendance)
                .build();

        return RapportDettesEntrepriseResponse.AnalyseTemporelle.builder()
            .distributionMensuelle(distributionMensuelle)
            .tendancesPaiement(tendancesPaiement)
            .build();
    }

    private String evaluerFournisseur(double tauxPaiement, long nombreRetards) {
        if (tauxPaiement >= 80 && nombreRetards == 0) {
            return "BON";
        } else if (tauxPaiement >= 50 || nombreRetards <= 2) {
            return "MOYEN";
        } else {
            return "CRITIQUE";
        }
    }

    private String determinerTendance(double tauxRespect) {
        if (tauxRespect >= 80) {
            return "EN_AMELIORATION";
        } else if (tauxRespect >= 60) {
            return "STABLE";
        } else {
            return "EN_DETERIORATION";
        }
    }

    private double arrondir(double valeur) {
        return Math.round(valeur * 100.0) / 100.0;
    }
    
    private boolean estDetteEnRetard(DettesFournisseur dette) {
        if (Boolean.TRUE.equals(dette.getEstPaye())) {
            return false;
        }
        
        return dette.getTranchesPaiement().stream()
            .anyMatch(this::estTrancheEnRetard);
    }
    
    private boolean estTrancheEnRetard(TranchePaiement tranche) {
        if (Boolean.TRUE.equals(tranche.getEstPaye())) {
            return false;
        }
        return tranche.getDateEcheance() != null && LocalDate.now().isAfter(tranche.getDateEcheance());
    }
}
