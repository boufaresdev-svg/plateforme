export enum AlertType {
  STOCK_FAIBLE = 'STOCK_FAIBLE',
  RUPTURE = 'RUPTURE',
  STOCK_ELEVE = 'STOCK_ELEVE',
  EXPIRATION = 'EXPIRATION',
  SEUIL_CRITIQUE = 'SEUIL_CRITIQUE',
  MOUVEMENT_SUSPECT = 'MOUVEMENT_SUSPECT',
  SYSTEM = 'SYSTEM',
  WARNING = 'WARNING',
  INFO = 'INFO',
  SUCCESS = 'SUCCESS'
}

export enum AlertPriority {
  LOW = 'LOW',
  MEDIUM = 'MEDIUM',
  HIGH = 'HIGH',
  CRITICAL = 'CRITICAL'
}

export enum AlertStatus {
  ACTIVE = 'ACTIVE',
  ACKNOWLEDGED = 'ACKNOWLEDGED',
  RESOLVED = 'RESOLVED',
  DISMISSED = 'DISMISSED'
}

export interface Alerte {
  id: string;
  type: AlertType;
  priority: AlertPriority;
  status: AlertStatus;
  titre: string;
  message: string;
  description?: string;
  articleId?: string;
  articleNom?: string;
  entrepotId?: string;
  entrepotNom?: string;
  quantiteActuelle?: number;
  seuilMinimum?: number;
  seuilCritique?: number;
  dateCreation: Date;
  dateModification?: Date;
  dateExpiration?: Date;
  utilisateurId?: string;
  utilisateurNom?: string;
  actions?: AlertAction[];
  metadata?: Record<string, any>;
  isRead: boolean;
  isArchived: boolean;
}

export interface AlertAction {
  id: string;
  label: string;
  action: string;
  icon?: string;
  color?: string;
  primary?: boolean;
}

export interface AlertFilter {
  type?: AlertType[];
  priority?: AlertPriority[];
  status?: AlertStatus[];
  articleId?: string;
  entrepotId?: string;
  dateDebut?: Date;
  dateFin?: Date;
  isRead?: boolean;
  isArchived?: boolean;
  searchTerm?: string;
  page?: number;
  size?: number;
  sortBy?: string;
  sortDirection?: string;
}

export interface AlertStats {
  total: number;
  active: number;
  acknowledged: number;
  resolved: number;
  critical: number;
  high: number;
  medium: number;
  low: number;
  byType: Record<AlertType, number>;
  recent: number;
}

export interface AlertNotification {
  id: string;
  alertId: string;
  type: 'email' | 'sms' | 'push' | 'system';
  recipient: string;
  message: string;
  dateSent: Date;
  status: 'pending' | 'sent' | 'failed';
  attempts: number;
}