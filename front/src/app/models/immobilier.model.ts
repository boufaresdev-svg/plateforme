export interface BienImmobilier {
  id: string;
  reference: string;
  nom: string;
  type: TypeBien;
  statut: StatutBien;
  adresse: string;
  ville: string;
  codePostal: string;
  surface: number; // en m²
  nombrePieces: number;
  etage?: number;
  ascenseur: boolean;
  parking: boolean;
  prixAchat?: number;
  valeurActuelle: number;
  loyerMensuel?: number;
  charges: number;
  taxeFonciere: number;
  assurance: number;
  dateAcquisition: Date;
  contrats: ContratLocation[];
  maintenances: MaintenanceImmobilier[];
  documents: string[];
  photos: string[];
  createdAt: Date;
  updatedAt: Date;
}

export interface ContratLocation {
  id: string;
  bienId: string;
  locataire: string;
  emailLocataire: string;
  telephoneLocataire: string;
  dateDebut: Date;
  dateFin: Date;
  loyerMensuel: number;
  depot: number;
  statut: StatutContrat;
  paiements: PaiementLoyer[];
}

export interface PaiementLoyer {
  id: string;
  contratId: string;
  mois: string;
  montant: number;
  datePaiement?: Date;
  statut: StatutPaiement;
}

export interface MaintenanceImmobilier {
  id: string;
  bienId: string;
  type: TypeMaintenance;
  description: string;
  cout: number;
  date: Date;
  prestataire: string;
  statut: StatutMaintenance;
}

export enum TypeBien {
  APPARTEMENT = 'Appartement',
  MAISON = 'Maison',
  BUREAU = 'Bureau',
  LOCAL_COMMERCIAL = 'Local Commercial',
  ENTREPOT = 'Entrepôt',
  TERRAIN = 'Terrain'
}

export enum StatutBien {
  LIBRE = 'Libre',
  LOUE = 'Loué',
  EN_VENTE = 'En Vente',
  EN_TRAVAUX = 'En Travaux',
  VENDU = 'Vendu'
}

export enum StatutContrat {
  ACTIF = 'Actif',
  EXPIRE = 'Expiré',
  RESILIE = 'Résilié',
  EN_COURS = 'En Cours'
}

export enum StatutPaiement {
  PAYE = 'Payé',
  EN_ATTENTE = 'En Attente',
  EN_RETARD = 'En Retard',
  IMPAYE = 'Impayé'
}

export enum TypeMaintenance {
  PREVENTIVE = 'Préventive',
  CORRECTIVE = 'Corrective',
  AMELIORATION = 'Amélioration',
  URGENCE = 'Urgence'
}

export enum StatutMaintenance {
  PLANIFIEE = 'Planifiée',
  EN_COURS = 'En Cours',
  TERMINEE = 'Terminée',
  ANNULEE = 'Annulée'
}

export interface ImmobilierStats {
  totalBiens: number;
  biensLoues: number;
  biensLibres: number;
  valeurPortefeuille: number;
  revenus: number;
  charges: number;
  rentabilite: number;
  tauxOccupation: number;
}