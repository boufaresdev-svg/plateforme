export interface Employee {
  id?: string;  // UUID from backend
  nomUtilisateur: string;
  email: string;
  motDePasse?: string;  // Only for creation
  prenom: string;
  nom: string;
  numeroTelephone?: string;
  departement?: string;
  poste?: string;
  statut: EmployeeStatus;
  roles: Role[];
  createdAt?: Date;
  updatedAt?: Date;
  derniereConnexion?: Date;
}

export interface Role {
  id?: string;  // UUID from backend
  nom: string;
  description?: string;
  permissions: Permission[];
  createdAt?: Date;
  updatedAt?: Date;
}

// Permission actions
export enum PermissionAction {
  CREER = 'CREER',
  LIRE = 'LIRE',
  MODIFIER = 'MODIFIER',
  SUPPRIMER = 'SUPPRIMER',
  EXPORTER = 'EXPORTER',
  IMPORTER = 'IMPORTER',
  LISTER_SENSIBLE = 'LISTER_SENSIBLE'
}

// Permission model - represents an action on a specific resource
export interface Permission {
  id?: string;  // Optional for consistency with other models
  ressource: string;  // e.g., "CLIENT", "FOURNISSEUR", "PROJET", etc.
  action: PermissionAction;
  description?: string;
  nomAffichage?: string;
  module?: string;  // Module grouping
}

export enum EmployeeStatus {
  ACTIF = 'ACTIF',
  INACTIF = 'INACTIF',
  SUSPENDU = 'SUSPENDU',
  EN_ATTENTE = 'EN_ATTENTE'
}

export interface EmployeeStats {
  totalEmployees: number;
  activeEmployees: number;
  inactiveEmployees: number;
  suspendedEmployees: number;
  pendingEmployees: number;
  totalRoles: number;
  totalPermissions: number;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

export interface PaginationParams {
  page: number;
  size: number;
  sortBy?: string;
  sortDirection?: 'ASC' | 'DESC';
  search?: string;
  status?: EmployeeStatus;
}
