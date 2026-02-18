export interface StockTrace {
  id: string;
  stockId: string;
  articleNom: string; // Article name from backend
  stockBatch?: string;
  entrepot: string;
  quantiteChanged: number;
  previousQuantity: number;
  userPerformedBy: string;
  dateReason: Date;
  unitPrice: number;
  performedAt: Date;
  actionType: StockActionType;
  motif: string;
  commentaire?: string;
  // Relations
  stock?: any; // Reference to stock item
  user?: any;  // Reference to user who performed action
}

export enum StockActionType {
  ENTREE = 'Entrée',
  SORTIE = 'Sortie',
  TRANSFERT = 'Transfert',
  AJUSTEMENT = 'Ajustement',
  AUGMENTATION = 'Augmentation',
  DIMINUTION = 'Diminution',
  INVENTAIRE = 'Inventaire',
  CORRECTION = 'Correction'
}

export interface StockTraceFilter {
  stockId?: string;
  dateDebut?: Date;
  dateFin?: Date;
  actionType?: StockActionType;
  utilisateur?: string;
  entrepot?: string;
}

export interface StockTraceStats {
  totalActions: number;
  actionsParType: { [key: string]: number };
  actionsParUtilisateur: { [key: string]: number };
  quantiteTotaleModifiee: number;
  valeurTotaleModifiee: number;
}