export interface SystemUser {
  id?: string;
  nomUtilisateur: string;
  motDePasse?: string;
  email: string;
  prenom: string;
  nom: string;
  numeroTelephone?: string;
  departement?: string;
  poste?: string;
  statut: UserStatus;
  roles?: Role[];
  derniereConnexion?: Date;
  failedLoginAttempts?: number;
  accountLockedUntil?: Date;
  createdAt?: Date;
  updatedAt?: Date;
  createdBy?: string;
  updatedBy?: string;
}

export enum UserStatus {
  ACTIF = 'ACTIF',
  INACTIF = 'INACTIF',
  EN_ATTENTE = 'EN_ATTENTE',
  SUSPENDU = 'SUSPENDU'
}

export interface Role {
  id?: string;
  nom: string;
  description?: string;
  permissions?: Permission[];
}

export interface Permission {
  id?: string;
  ressource: string;
  action: PermissionAction;
  nomAffichage?: string;
  description?: string;
  module?: Module;
}

export enum PermissionAction {
  CREER = 'CREER',
  LIRE = 'LIRE',
  MODIFIER = 'MODIFIER',
  SUPPRIMER = 'SUPPRIMER',
  EXPORTER = 'EXPORTER',
  IMPORTER = 'IMPORTER',
  LISTER_SENSIBLE = 'LISTER_SENSIBLE'
}

export enum Module {
  GESTION_UTILISATEURS = 'GESTION_UTILISATEURS',
  GESTION_CLIENTS = 'GESTION_CLIENTS',
  GESTION_FOURNISSEURS = 'GESTION_FOURNISSEURS',
  GESTION_PROJETS = 'GESTION_PROJETS',
  GESTION_RH = 'GESTION_RH',
  GESTION_FORMATIONS = 'GESTION_FORMATIONS',
  GESTION_VEHICULES = 'GESTION_VEHICULES',
  GESTION_STOCK = 'GESTION_STOCK',
  SYSTEME = 'SYSTEME'
}

export interface ModuleInfo {
  code: string;
  displayName: string;
  permissionCount: number;
}

export interface PermissionsByModule {
  [key: string]: Permission[];
}

export interface UserSearchQuery {
  id?: string;
  nomUtilisateur?: string;
  email?: string;
  prenom?: string;
  nom?: string;
  departement?: string;
  poste?: string;
  statut?: UserStatus;
  roleIds?: string[];
  page?: number;
  size?: number;
  sortBy?: string;
  sortDirection?: 'ASC' | 'DESC';
}

export interface UserStats {
  totalUsers: number;
  activeUsers: number;
  inactiveUsers: number;
  suspendedUsers: number;
  pendingUsers: number;
}
