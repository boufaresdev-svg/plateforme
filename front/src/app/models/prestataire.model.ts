export interface Prestataire {
  id: string;
  nom: string;
  raisonSociale: string;
  siret?: string;
  secteur: SecteurPrestataire;
  type: TypePrestataire;
  statut: StatutPrestataire;
  adresse: string;
  ville: string;
  codePostal: string;
  pays: string;
  telephone: string;
  email: string;
  siteWeb?: string;
  contactPrincipal: ContactPrestataire;
  contacts: ContactPrestataire[];
  specialites: string[];
  certifications: string[];
  evaluations: EvaluationPrestataire[];
  contrats: ContratPrestataire[];
  conditions: ConditionsPrestataire;
  documents: string[];
  createdAt: Date;
  updatedAt: Date;
}

export interface ContactPrestataire {
  id: string;
  nom: string;
  prenom: string;
  poste: string;
  telephone: string;
  email: string;
  principal: boolean;
}

export interface EvaluationPrestataire {
  id: string;
  prestataireId: string;
  date: Date;
  qualite: number; // 1-5
  delais: number; // 1-5
  service: number; // 1-5
  prix: number; // 1-5
  noteGlobale: number; // 1-5
  commentaires: string;
  evaluateur: string;
}

export interface ContratPrestataire {
  id: string;
  prestataireId: string;
  numero: string;
  type: TypeContratPrestataire;
  dateDebut: Date;
  dateFin: Date;
  montant: number;
  statut: StatutContratPrestataire;
  conditions: string;
}

export interface ConditionsPrestataire {
  delaiPaiement: number; // en jours
  remise: number; // en %
  fraisDeplacementKm: number;
  garantie: number; // en mois
  disponibiliteUrgence: boolean;
}

export enum SecteurPrestataire {
  INFORMATIQUE = 'Informatique',
  MAINTENANCE = 'Maintenance',
  NETTOYAGE = 'Nettoyage',
  SECURITE = 'Sécurité',
  FORMATION = 'Formation',
  CONSEIL = 'Conseil',
  CONSTRUCTION = 'Construction',
  TRANSPORT = 'Transport',
  AUTRE = 'Autre'
}

export enum TypePrestataire {
  ENTREPRISE = 'Entreprise',
  INDEPENDANT = 'Indépendant',
  COOPERATIVE = 'Coopérative',
  ASSOCIATION = 'Association'
}

export enum StatutPrestataire {
  ACTIF = 'Actif',
  INACTIF = 'Inactif',
  SUSPENDU = 'Suspendu',
  BLACKLISTE = 'Blacklisté'
}

export enum TypeContratPrestataire {
  PONCTUEL = 'Ponctuel',
  CADRE = 'Cadre',
  EXCLUSIF = 'Exclusif',
  MAINTENANCE = 'Maintenance'
}

export enum StatutContratPrestataire {
  ACTIF = 'Actif',
  EXPIRE = 'Expiré',
  RESILIE = 'Résilié',
  EN_NEGOCIATION = 'En Négociation'
}

export interface PrestataireStats {
  totalPrestataires: number;
  prestatairesActifs: number;
  contratsActifs: number;
  montantContrats: number;
  noteMoyenne: number;
  specialitesUniques: number;
  tauxSatisfaction: number;
  interventionsRecentes: number;
}