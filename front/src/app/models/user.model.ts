export interface User {
  id?: string;
  nomUtilisateur: string;
  email?: string;
  prenom?: string;
  nom?: string;
  numeroTelephone?: string;
  departement?: string;
  poste?: string;
  statut?: string;
  roles?: Role[];
  derniereConnexion?: string;
  isAuthenticated: boolean;
}

export interface Role {
  id?: string;
  nom: string;
  description?: string;
  permissions?: Permission[];
}

export interface Permission {
  id?: string;
  nom: string;
  description?: string;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  accessToken: string;
  refreshToken: string;
  user: User;
}