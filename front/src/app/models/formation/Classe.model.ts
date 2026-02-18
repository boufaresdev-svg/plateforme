import { Apprenant } from './Apprenant.model';
import { PlanFormation } from './PlanFormation.model';

export interface Classe {
  id?: number;
  nom: string;
  code?: string;
  description?: string;
  capaciteMax?: number;
  isActive?: boolean;
  dateDebutAcces?: string;
  dateFinAcces?: string;
  isAccessible?: boolean;
  createdAt?: Date;
  updatedAt?: Date;
  enrollmentCount?: number;
  isFull?: boolean;
  
  // Formation association
  formationId?: number;
  formationTheme?: string;
  formation?: {
    id: number;
    theme: string;
  };
  
  // Plan formation association
  planFormationId?: number;
  planFormationTitre?: string;
  planFormation?: {
    id: number;
    titre: string;
  };
  
  // Apprenants in class
  apprenants?: ApprenantBasic[];
}

export interface ApprenantBasic {
  id: number;
  nom: string;
  prenom?: string;
  email?: string;
  matricule?: string;
}

export interface ClasseStats {
  total: number;
  active: number;
}

export interface ClasseSearchResponse {
  content: Classe[];
  currentPage: number;
  totalItems: number;
  totalPages: number;
}
