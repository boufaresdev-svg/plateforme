export interface SousTraitance {
  id: string;
  titre: string;
  description: string;
  type: TypeSousTraitance;
  priorite: PrioriteSousTraitance;
  statut: StatutSousTraitance;
  sousTraitant: string;
  contactSousTraitant: string;
  telephoneSousTraitant: string;
  emailSousTraitant: string;
  domaine: string;
  emplacement: string;
  demandeur: string;
  dateCreation: Date;
  dateDebutPrevue: Date;
  dateFinPrevue: Date;
  dateDebutEffective?: Date;
  dateFinEffective?: Date;
  coutEstime: number;
  coutReel?: number;
  prestations: PrestationSousTraitance[];
  interventions: InterventionSousTraitance[];
}

export interface PrestationSousTraitance {
  id: string;
  nom: string;
  quantite: number;
  prixUnitaire: number;
  sousTraitant?: string;
  reference?: string;
}

export interface InterventionSousTraitance {
  id: string;
  sousTraitanceId: string;
  technicien: string;
  dateIntervention: Date;
  duree: number; // en heures
  description: string;
  statut: StatutIntervention;
  observations?: string;
}

export enum TypeSousTraitance {
  INFORMATIQUE = 'Informatique',
  MAINTENANCE = 'Maintenance',
  NETTOYAGE = 'Nettoyage',
  SECURITE = 'Sécurité',
  FORMATION = 'Formation',
  CONSEIL = 'Conseil',
  AUTRE = 'Autre'
}

export enum PrioriteSousTraitance {
  BASSE = 'Basse',
  NORMALE = 'Normale',
  HAUTE = 'Haute',
  CRITIQUE = 'Critique'
}

export enum StatutSousTraitance {
  PLANIFIEE = 'Planifiée',
  EN_COURS = 'En Cours',
  TERMINEE = 'Terminée',
  ANNULEE = 'Annulée',
  EN_ATTENTE = 'En Attente'
}

export enum StatutIntervention {
  PREVUE = 'Prévue',
  EN_COURS = 'En Cours',
  TERMINEE = 'Terminée',
  REPORTEE = 'Reportée'
}

export interface SousTraitanceStats {
  totalSousTraitances: number;
  sousTraitancesEnCours: number;
  sousTraitancesPlanifiees: number;
  coutTotalSousTraitance: number;
  nombreSousTraitants: number;
  tauxSatisfaction: number;
}