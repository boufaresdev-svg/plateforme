export interface RapportDettesEntreprise {
  // Informations générales
  infosGenerales: InfosGenerales;
  // Synthèse financière
  syntheseFinanciere: SyntheseFinanciere;
  // Analyse par fournisseur
  analyseParFournisseur: AnalyseFournisseur[];
  // Analyse statut
  analyseStatut: AnalyseStatut;
  // Analyse retards
  analyseRetards: AnalyseRetards;
  // Analyse temporelle
  analyseTemporelle: AnalyseTemporelle;
}

export interface InfosGenerales {
  dateGeneration: string;
  periodeDebut: string | null;
  periodeFin: string | null;
  nombreTotalDettes: number;
  nombreFournisseursActifs: number;
}

export interface SyntheseFinanciere {
  montantTotalDettes: number;
  montantTotalPaye: number;
  montantTotalImpaye: number;
  montantEnRetard: number;
  tauxPaiement: number;
  montantMoyenParDette: number;
  montantMoyenParFournisseur: number;
}

export interface AnalyseFournisseur {
  fournisseurId: string;
  nomFournisseur: string;
  infoContact: string;
  nombreDettes: number;
  montantTotal: number;
  montantPaye: number;
  montantImpaye: number;
  nombreDettesEnRetard: number;
  montantEnRetard: number;
  tauxPaiement: number;
  evaluation: 'BON' | 'MOYEN' | 'CRITIQUE';
  ancienneteMoyenneJours: number;
}

export interface AnalyseStatut {
  nombreDettesPayees: number;
  nombreDettesImpayes: number;
  montantDettesPayees: number;
  montantDettesImpayes: number;
  pourcentageDettesPayees: number;
  pourcentageDettesImpayes: number;
}

export interface AnalyseRetards {
  nombreDettesEnRetard: number;
  montantTotalEnRetard: number;
  pourcentageDettesEnRetard: number;
  retardMoyenJours: number;
  retardMaximumJours: number;
  top5DettesEnRetard: DetteEnRetard[];
}

export interface DetteEnRetard {
  detteId: string;
  numeroFacture: string;
  fournisseurNom: string;
  montant: number;
  dateEcheance: string;
  joursRetard: number;
}

export interface AnalyseTemporelle {
  distributionMensuelle: DistributionMensuelle;
  tendancesPaiement: TendancesPaiement;
}

export interface DistributionMensuelle {
  donneesParMois: DonneesMensuelle[];
}

export interface DonneesMensuelle {
  mois: string;
  nombreDettes: number;
  montantTotal: number;
  montantPaye: number;
  montantImpaye: number;
}

export interface TendancesPaiement {
  delaiMoyenPaiementJours: number;
  tauxRespectEcheances: number;
  tendanceGenerale: 'EN_AMELIORATION' | 'STABLE' | 'EN_DETERIORATION';
}

export interface RapportFilterParams {
  dateDebut?: string | null;
  dateFin?: string | null;
  inclureDettesPayees?: boolean;
  fournisseurIds?: string[] | null;
}
