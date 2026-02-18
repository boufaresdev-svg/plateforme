export interface SOP {
  id: string;
  titre: string;
  description: string;
  code: string; // SOP-001, SOP-002, etc.
  version: string;
  categorie: CategorieSOP;
  departement: string;
  processus: string;
  statut: StatutSOP;
  auteur: string;
  approbateur: string;
  dateCreation: Date;
  dateApprobation?: Date;
  dateRevision?: Date;
  prochainRevision: Date;
  etapes: EtapeSOP[];
  documents: DocumentSOP[];
  formations: string[];
  tags: string[];
  createdAt: Date;
  updatedAt: Date;
}

export interface EtapeSOP {
  id: string;
  sopId: string;
  numero: number;
  titre: string;
  description: string;
  responsable: string;
  dureeEstimee: number; // en minutes
  outils: string[];
  controles: string[];
  risques: string[];
  completed?: boolean;
}

export interface DocumentSOP {
  id: string;
  sopId: string;
  nom: string;
  type: TypeDocument;
  url: string;
  version: string;
  dateAjout: Date;
}

export enum CategorieSOP {
  QUALITE = 'Qualité',
  SECURITE = 'Sécurité',
  PRODUCTION = 'Production',
  MAINTENANCE = 'Maintenance',
  ADMINISTRATION = 'Administration',
  RH = 'Ressources Humaines',
  INFORMATIQUE = 'Informatique',
  AUTRE = 'Autre'
}

export enum StatutSOP {
  BROUILLON = 'Brouillon',
  EN_REVISION = 'En Révision',
  APPROUVE = 'Approuvé',
  ACTIF = 'Actif',
  OBSOLETE = 'Obsolète',
  ARCHIVE = 'Archivé'
}

export enum TypeDocument {
  PROCEDURE = 'Procédure',
  FORMULAIRE = 'Formulaire',
  CHECKLIST = 'Checklist',
  SCHEMA = 'Schéma',
  VIDEO = 'Vidéo',
  AUTRE = 'Autre'
}

export interface SOPStats {
  totalSOPs: number;
  sopsActifs: number;
  sopsEnRevision: number;
  sopsObsoletes: number;
  formationsLiees: number;
  tauxConformite: number;
  revisionsEnRetard: number;
  documentsAttaches: number;
}