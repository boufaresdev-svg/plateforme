export interface Dossier {
  id: string;
  nom: string;
  description: string;
  categorie: CategorieDossier;
  statut: StatutDossier;
  proprietaire: string;
  departement: string;
  dateCreation: Date;
  dateModification: Date;
  dateArchivage?: Date;
  tags: string[];
  documents: DocumentDossier[];
  acces: AccesDossier[];
  taille: number; // en MB
}

export interface DocumentDossier {
  id: string;
  dossierId: string;
  nom: string;
  type: TypeDocument;
  taille: number; // en MB
  chemin: string;
  version: string;
  dateAjout: Date;
  ajoutePar: string;
  description?: string;
  estConfidentiel: boolean;
}

export interface AccesDossier {
  id: string;
  dossierId: string;
  utilisateur: string;
  typeAcces: TypeAcces;
  dateAccorde: Date;
  accordePar: string;
  dateExpiration?: Date;
}

export enum CategorieDossier {
  ADMINISTRATIF = 'Administratif',
  JURIDIQUE = 'Juridique',
  FINANCIER = 'Financier',
  TECHNIQUE = 'Technique',
  RH = 'Ressources Humaines',
  COMMERCIAL = 'Commercial',
  PROJET = 'Projet',
  AUTRE = 'Autre'
}

export enum StatutDossier {
  ACTIF = 'Actif',
  ARCHIVE = 'Archivé',
  BROUILLON = 'Brouillon',
  EN_REVISION = 'En Révision',
  CONFIDENTIEL = 'Confidentiel'
}

export enum TypeDocument {
  PDF = 'PDF',
  WORD = 'Word',
  EXCEL = 'Excel',
  IMAGE = 'Image',
  VIDEO = 'Vidéo',
  AUTRE = 'Autre'
}

export enum TypeAcces {
  LECTURE = 'Lecture',
  ECRITURE = 'Écriture',
  ADMIN = 'Administrateur'
}

export interface DossierStats {
  totalDossiers: number;
  dossiersActifs: number;
  dossiersArchives: number;
  totalDocuments: number;
  tailleTotal: number; // en MB
  dossiersParCategorie: { [key: string]: number };
}