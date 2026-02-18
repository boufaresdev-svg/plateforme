import { PlanFormation } from "./PlanFormation.model";

export interface Apprenant {
  id?: number;
  idApprenant?: number;
  matricule?: string;
  nom: string;
  prenom?: string;
  adresse?: string;
  telephone?: string;
  email?: string;
  prerequis?: string;
  password?: string;
  isBlocked?: boolean;
  isStaff?: boolean;
  isActive?: boolean;
  createdAt?: Date;
  updatedAt?: Date;
  lastLogin?: Date;
  statusInscription?: StatusInscription;
  planFormation?: PlanFormation;
  idPlanFormation?: number;
  planFormationId?: number;
  planFormationTitre?: string;
}

export enum StatusInscription {
  INSCRIT = 'INSCRIT',
  ANNULEE = 'Annulee',
  CONFIRME = 'CONFIRME',
  REFUSE = 'REFUSE'
}

export interface ApprenantStats {
  total: number;
  active: number;
  blocked: number;
}

export interface ApprenantSearchResponse {
  content: Apprenant[];
  currentPage: number;
  totalItems: number;
  totalPages: number;
}

export interface CsvImportResponse {
  imported: number;
  errors: { line: string; error: string }[];
  credentials?: { matricule: string; email: string; password: string }[];
}

export interface ApprenantClassesResponse {
  apprenantId: number;
  totalClasses: number;
  classes: ApprenantClasseInfo[];
}

export interface ApprenantClasseInfo {
  id: number;
  nom: string;
  code: string;
  description?: string;
  capaciteMax?: number;
  isActive: boolean;
  dateDebutAcces?: string;
  dateFinAcces?: string;
  nombreInscrits: number;
  formation?: { id: number; nom: string };
  planFormation?: { id: number; nom: string };
}

export interface ApprenantEnrollmentResponse {
  apprenantId: number;
  classeId: number;
  enrolled: boolean;
  classe?: { nom: string; code: string; isActive: boolean };
  error?: string;
}


